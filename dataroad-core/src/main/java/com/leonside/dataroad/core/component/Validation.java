package com.leonside.dataroad.core.component;

import java.io.Serializable;

/**
 * @author leon
 */
public interface Validation extends Serializable {

    default boolean validate(){
        return true;
    }
}
