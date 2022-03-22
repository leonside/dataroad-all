package com.leonside.dataroad.common.config;

import com.leonside.dataroad.common.utils.JsonUtil;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author leon
 */
@Data
public class Options implements Serializable {

    public static final String PLUGIN_DIR_NAME = "plugins";
    /**
     * 流程配置文件路径，支持classpath:、file:、http:几种资源类型
     */
    private String conf;
    /**
     * 插件根路径,非必填，
     */
    private String pluginRootDir;

    /**
     * 扩展lib路径，支持file:、http:几种资源类型。当通过dashboard提交任务时应用，非必填
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

    public static Options of(Map<String, String> parameters) throws UnsupportedEncodingException {

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
            String decodeConfProp = URLDecoder.decode(confProp, "UTF-8");
            Map<String,Object> confMap = JsonUtil.getInstance().readJson(decodeConfProp, HashMap.class);
            Map<String,String> convertedMap = new HashMap<>();
            confMap.entrySet().stream().forEach(entry->{
                convertedMap.put(entry.getKey(), entry.getValue().toString());
            });
            options.setConfProp(convertedMap);
        }

        return options;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String encode = URLEncoder.encode("{\"parallelism.default\":2}", "UTF-8");
        System.out.println(encode);
    }
}
