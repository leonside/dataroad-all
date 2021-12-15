package com.leonside.dataroad.flink.lookup.config;

import com.leonside.dataroad.core.component.Validation;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
@Data
public class BaseLookupConfig implements Validation , Serializable {

    private List<Map<String,Object>> directData;

    private Map<String,String> joinColumns;

    private String[] columns;

    private int cacheMaxrows = -1;
    private int cacheTtl = -1;
    /**
     * 包含 all、lru
     */
    private String cacheType;
//    private int cachePeriod ;


    @Override
    public boolean validate() {
        return Validation.super.validate();
    }
}
