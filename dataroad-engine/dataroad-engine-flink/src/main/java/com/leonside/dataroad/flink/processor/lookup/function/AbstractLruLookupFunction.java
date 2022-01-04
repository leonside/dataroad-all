package com.leonside.dataroad.flink.processor.lookup.function;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.leonside.dataroad.flink.processor.lookup.config.BaseLookupConfig;
import com.leonside.dataroad.flink.utils.RowUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author leon
 */
public abstract class AbstractLruLookupFunction extends RichAsyncFunction<Row, Row> implements Serializable {

    public static Logger logger = LoggerFactory.getLogger(AbstractAllLookupFunction.class);

    protected BaseLookupConfig baseLookupConfig;

    protected Cache<String, Row> cache;

    protected String[] keyColumns;

    protected String[] valueColumns;

    public AbstractLruLookupFunction(BaseLookupConfig baseLookupConfig) {
        this.baseLookupConfig = baseLookupConfig;
        this.keyColumns = baseLookupConfig.getJoinColumns().keySet().toArray(new String[]{});
        this.valueColumns = baseLookupConfig.getJoinColumns().values().toArray(new String[]{});
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        if(baseLookupConfig.getCacheType().equals(BaseLookupConfig.CacheType.lru.name())){
            CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();
            cacheBuilder.maximumSize(baseLookupConfig.getCacheMaxrows());
            cacheBuilder.expireAfterAccess(baseLookupConfig.getCacheTtl(), TimeUnit.MILLISECONDS);
            cache = cacheBuilder.build();
        }else{
            logger.debug("Disabling caching");
        }
    }

    public void putCache(String key, Row value) {
        if(cache != null){
            cache.put(key, value);
        }
    }

    public void dealMissKey(ResultFuture<Row> future) {
        try {
            future.complete(Collections.emptyList());
        } catch (Exception e) {
            logger.error("deal Miss key error", e);
        }
    }

    public void dealFillDataError(ResultFuture<Row> resultFuture, Exception e) {
        logger.error("dealFillDataError", e);
        dealMissKey(resultFuture);
    }


    @Override
    public void asyncInvoke(Row row, ResultFuture<Row> resultFuture) throws Exception {

        if(cache == null){
            doAsyncInvoke(row, resultFuture);
            return;
        }

        String keyValues = buildCacheKey(row,keyColumns);
        Row fetchRow = cache.getIfPresent(keyValues);
        if(fetchRow != null){
            logger.debug("cache match, cache key[{}]", keyValues);
            resultFuture.complete(Lists.newArrayList(RowUtils.combineRowWithNames(row, fetchRow, valueColumns)));
        }else{
            doAsyncInvoke(row, resultFuture);
        }
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

    protected abstract void doAsyncInvoke(Row row, ResultFuture<Row> resultFuture) throws InterruptedException;

    @Override
    public void timeout(Row input, ResultFuture<Row> resultFuture) throws Exception {
        super.timeout(input, resultFuture);
        resultFuture.complete(Lists.newArrayList(input));
    }

}
