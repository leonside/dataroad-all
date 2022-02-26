package com.leonside.dataroad.common.context;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.leonside.dataroad.common.config.Validation;
import lombok.Data;

import java.io.Serializable;

/**
 * @author leon
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobSetting implements Serializable , Validation {

    private String name = "defaultJob";

    private boolean isLocal = false;
//    private String monitorUrls;

    private LogConfig log = LogConfig.defaultConfig();

    private RestoreConfig restore = RestoreConfig.defaultConfig();

    private SpeedConfig speed = SpeedConfig.defaultConfig();

    private ErrorLimitConfig errorLimit = ErrorLimitConfig.defaultConfig();


    public void setIsLocal(boolean local) {
        isLocal = local;
    }

    @Override
    public boolean validate() {

        restore.validate();

        return true;
    }
}
