package com.leonside.dataroad.common.context;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @author leon
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogConfig implements Serializable {

    private boolean isLogger = false;
    private String level = "info";

    public static LogConfig defaultConfig() {
        return new LogConfig();
    }
//    private String path = null;
//    private String pattern = null;

    public void setIsLogger(boolean logger) {
        isLogger = logger;
    }
}
