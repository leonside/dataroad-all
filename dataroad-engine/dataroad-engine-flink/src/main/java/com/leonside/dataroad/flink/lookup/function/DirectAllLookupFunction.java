package com.leonside.dataroad.flink.lookup.function;

import com.leonside.dataroad.common.exception.JobConfigException;
import com.leonside.dataroad.flink.lookup.config.BaseLookupConfig;
import com.leonside.dataroad.flink.utils.RowUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.types.Row;

import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
public class DirectAllLookupFunction extends AbstractAllLookupFunction  {

    public DirectAllLookupFunction(BaseLookupConfig baseLookupConfig) {
        super(baseLookupConfig);
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        loadAllData();
    }

    @Override
    protected void loadAllData() {
        List<Map<String, Object>> directData = baseLookupConfig.getDirectData();
        if(CollectionUtils.isEmpty(directData)){
            throw new JobConfigException("DirectData for DirectLookup cannot be null");
        }

        directData.stream().forEach(it->{
            Row row = RowUtils.toRowWithNames(it);

            buildCache(row);
        });
    }

    public static class DirectLookupFunctionBuilder {

        private BaseLookupConfig lookupConfig;

        public DirectLookupFunctionBuilder lookupConfig(BaseLookupConfig lookupConfig){
            this.lookupConfig = lookupConfig;
            return this;
        }

        public DirectAllLookupFunction build() {
            return new DirectAllLookupFunction(lookupConfig);
        }
    }

}
