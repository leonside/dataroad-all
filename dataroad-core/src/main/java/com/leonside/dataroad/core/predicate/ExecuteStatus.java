package com.leonside.dataroad.core.predicate;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * @author leon
 */
public class ExecuteStatus implements Serializable {


    public String name;

    public boolean ismatch(String matchValue){
        return StringUtils.equals(name, matchValue);
    }

    public ExecuteStatus(String name) {
        this.name = name;
    }
}
