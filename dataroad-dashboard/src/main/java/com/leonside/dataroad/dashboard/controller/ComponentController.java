package com.leonside.dataroad.dashboard.controller;

import com.google.common.collect.Lists;
import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.common.config.ConfigKey;
import com.leonside.dataroad.common.extension.ExtensionLoader;
import com.leonside.dataroad.common.spi.Component;
import com.leonside.dataroad.common.utils.ConfigBeanUtils;
import com.leonside.dataroad.common.utils.EnumUtils;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentType;
import com.leonside.dataroad.dashboard.builder.ComponentParameterBuilder;
import com.leonside.dataroad.dashboard.configuration.DataroadProperties;
import com.leonside.dataroad.dashboard.domian.CodeRecord;
import com.leonside.dataroad.dashboard.domian.ComponentParameter;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author leon
 */
@RestController
public class ComponentController {

    @Autowired
    private DataroadProperties dataroadProperties;

    @GetMapping("/api/components/{type}")
    public List<CodeRecord> listPlugins(@PathVariable("type") String pluginType) {

        ExtensionLoader extensionLoader = ExtensionLoader.getExtensionLoader(ComponentType.valueOf(pluginType).getSpi());

        List<CodeRecord> collect = (List<CodeRecord>) extensionLoader.getExtensionMaps()
                .entrySet()
                .stream()
                .map(entry -> new CodeRecord(((Map.Entry<String, Component>) entry).getKey(), ((Map.Entry<String, Component>) entry).getValue().description()))
                .collect(Collectors.toList());

        return collect;
    }

    @GetMapping("/api/component/{type}/{pluginName}/parameter")
    public List<ComponentParameter> listPluginParameter(@PathVariable("type") String pluginType,@PathVariable("pluginName") String pluginName) {

        Object component = ExtensionLoader.getExtensionLoader(ComponentType.valueOf(pluginType).getSpi())
                .getExtension(pluginName);

        List<ComponentParameter> componentParameters = new ArrayList<>();

        if(component instanceof ComponentInitialization){
            Class<? extends BaseConfig> configClass = ((ComponentInitialization) component).configClass();
            return ComponentParameterBuilder.builder().configClass(configClass).build();
        }

        return componentParameters;
    }

    @GetMapping("/api/component/archive/{componentType}/{jarFileName}")
    public void loadComponentArchive(@PathVariable("componentType") String componentType,@PathVariable("jarFileName") String jarFileName, HttpServletResponse response) throws IOException {
        OutputStream os = response.getOutputStream();
        try {
            response.reset();
            response.setHeader("Cache-Control", "private");
            response.setHeader("Pragma", "private");
//            response.setContentType(contentType + ";charset=utf-8");
            String filePath = dataroadProperties.getDataroadPluginPath() + File.separator + componentType + File.separator + jarFileName;
            response.setHeader("Content-disposition", "attachment; filename=" + jarFileName +"");
            os.write(FileUtils.readFileToByteArray(new File(filePath)));
            os.flush();

        } finally {
            if (os != null) {
                os.flush();
                os.close();
            }
        }
    }

}
