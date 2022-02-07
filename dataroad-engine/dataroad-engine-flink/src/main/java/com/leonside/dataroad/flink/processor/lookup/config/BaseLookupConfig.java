package com.leonside.dataroad.flink.processor.lookup.config;

import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.common.config.ConfigKey;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
@Data
public class BaseLookupConfig extends BaseConfig  {

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


    public BaseLookupConfig(Map<String, Object> parameter) {
        super(parameter);
    }

    @Override
    public boolean validate() {
        return super.validate();
    }

    @Override
    public Class<? extends ConfigKey> bindConfigKey() {
        return BaseLookupConfigKey.class;
    }

    public enum CacheType{
        lru, all,none
    }
}
