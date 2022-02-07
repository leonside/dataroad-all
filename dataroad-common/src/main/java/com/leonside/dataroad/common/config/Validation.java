package com.leonside.dataroad.common.config;

import java.io.Serializable;

/**
 * @author leon
 */
public interface Validation extends Serializable {

    default boolean validate(){
        return true;
    }
}
