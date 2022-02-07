package com.leonside.dataroad.common.context;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @author leon
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobSetting implements Serializable {

    private String name = "defaultJob";

    private boolean isLocal = true;
//    private String monitorUrls;

    private LogConfig log = LogConfig.defaultConfig();

    private RestoreConfig restore = RestoreConfig.defaultConfig();

    private SpeedConfig speed = SpeedConfig.defaultConfig();

    private ErrorLimitConfig errorLimit = ErrorLimitConfig.defaultConfig();

}
