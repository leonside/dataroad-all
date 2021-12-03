package com.leonside.dataroad.common.context;

import lombok.Data;

import java.io.Serializable;

/**
 * @author leon
 */
@Data
public class JobSetting implements Serializable {

    private String name = "defaultJob";

    private String monitorUrls;

    private LogConfig log = LogConfig.defaultConfig();

    private RestoreConfig restore = RestoreConfig.defaultConfig();

    private SpeedConfig speed = SpeedConfig.defaultConfig();

    private ErrorLimitConfig errorLimit = ErrorLimitConfig.defaultConfig();

}
