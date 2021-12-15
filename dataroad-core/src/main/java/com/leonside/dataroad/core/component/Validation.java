package com.leonside.dataroad.core.component;

/**
 * @author leon
 */
public interface Validation {

    default boolean validate(){
        return true;
    }
}
