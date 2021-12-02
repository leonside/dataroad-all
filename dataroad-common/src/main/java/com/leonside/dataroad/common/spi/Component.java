package com.leonside.dataroad.common.spi;

import java.io.Serializable;

/**
 * @author leon
 */
public interface Component extends Serializable {

    String getName();

    default void setName(String name){}

}
