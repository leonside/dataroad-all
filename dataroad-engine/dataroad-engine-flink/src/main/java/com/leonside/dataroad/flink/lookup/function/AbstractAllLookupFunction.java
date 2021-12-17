package com.leonside.dataroad.flink.lookup.function;

import com.leonside.dataroad.flink.lookup.config.BaseLookupConfig;
import com.leonside.dataroad.flink.utils.RowUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author leon
 */
public abstract class AbstractAllLookupFunction extends RichMapFunction<Row, Row> implements Serializable {

    public static Logger logger = LoggerFactory.getLogger(AbstractAllLookupFunction.class);

    protected Map<String, Row> cache = new ConcurrentHashMap<>();

    protected BaseLookupConfig baseLookupConfig;

    protected String[] keyColumns;

    protected String[] valueColumns;

    public <T extends BaseLookupConfig> AbstractAllLookupFunction(T baseLookupConfig) {
        this.baseLookupConfig = baseLookupConfig;
        this.keyColumns = baseLookupConfig.getJoinColumns().keySet().toArray(new String[]{});
        this.valueColumns = baseLookupConfig.getJoinColumns().values().toArray(new String[]{});

    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        cache = new ConcurrentHashMap<>();

        loadAllData();
    }

    protected void buildCache(Row row) {
        cache.putIfAbsent(buildCacheKey(row, valueColumns), row);
    }


    protected abstract void loadAllData();

    @Override
    public Row map(Row row) throws Exception {

        String keyValues = buildCacheKey(row, keyColumns);

        Row cacheRow = cache.get(keyValues);

        return RowUtils.combineRowWithNames(row, cacheRow, valueColumns);
    }

    protected String buildCacheKey(Row row, String[] columns) {
        String keyValues = Arrays.stream(columns)
                .map(key -> RowUtils.getStringField(row, key))
                .collect(Collectors.joining("_"));

        if(StringUtils.isEmpty(keyValues)){
            logger.warn("The cache Key obtained is null for row :" + row);
        }

        return keyValues;
    }
}
