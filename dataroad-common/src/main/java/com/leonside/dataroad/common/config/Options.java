package com.leonside.dataroad.common.config;

import com.leonside.dataroad.common.utils.JsonUtil;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leon
 */
@Data
public class Options {

    public static final String PLUGIN_DIR_NAME = "plugins";
    /**
     * 流程配置文件路径，支持classpath:、filesystem:、http:几种资源类型
     */
    private String conf;
    /**
     * 插件根路径,非必填，
     */
    private String pluginRootDir;

    /**
     * 扩展lib路径，支持filesystem:、http:几种资源类型。当通过dashboard提交任务时应用，非必填
     */
    private String[] extLibPath;
    /**
     * jobName，非必填.此处优先级低于配置文件中的jobName
     */
    private String jobName;
    /**
     * 官方原生的配置参数，如flink.非必填
     */
    private Map<String,String> confProp;

    public static String getPluginDir(String pluginRootDir){
        if(StringUtils.isEmpty(pluginRootDir)){
            return null;
        }
        return pluginRootDir.endsWith(File.separator) ? pluginRootDir + PLUGIN_DIR_NAME : pluginRootDir + File.separator + PLUGIN_DIR_NAME;
    }

    public static Options of(Map<String, String> parameters) {

        Options options = new Options();

        options.setConf(parameters.get("conf"));
        //插件根路径
        options.setPluginRootDir(parameters.get("pluginRootDir"));
        //扩展Lib路径，实现动态Lib加载，多个逗号隔开
        String extLibPath = parameters.get("extLibPath");
        if(StringUtils.isNotEmpty(extLibPath)){
            options.setExtLibPath(extLibPath.split(","));
        }
        options.setJobName(parameters.get("jobName"));

        String confProp = parameters.get("confProp");
        if(StringUtils.isNotEmpty(confProp)){
            options.setConfProp(JsonUtil.getInstance().readJson(confProp, HashMap.class));
        }

        return options;
    }
}
