package com.leonside.dataroad.flink.processor.lookup.config;

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

    public List<Map<String,Object>> directData;

    public Map<String,String> joinColumns;

    public String[] columns;

    public int cacheMaxrows;
    public int cacheTtl;
    /**
     * 包含 all、lru
     */
    public String cacheType;
//    private int cachePeriod ;


    @Override
    public boolean validate() {
        return Validation.super.validate();
    }

    public enum CacheType{
        lru, all,none
    }
}
