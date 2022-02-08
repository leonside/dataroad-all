
package com.leonside.dataroad.plugin.es.reader;

import com.google.gson.Gson;
import com.leonside.dataroad.common.constant.JobCommonConstant;
import com.leonside.dataroad.common.spi.ItemReader;
import com.leonside.dataroad.common.utils.ParameterUtils;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.flink.reader.BaseItemReader;
import com.leonside.dataroad.plugin.es.config.EsReaderConfig;
import com.leonside.dataroad.plugin.es.config.EsReaderConfigKey;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Reader plugin of ElasticSearch
 *
 */
@SuppressWarnings("uncheck")
public class EsReader extends BaseItemReader implements ItemReader<FlinkExecuteContext, DataStream<Row>>, ComponentInitialization<FlinkExecuteContext,EsReaderConfig> {

    private static Logger LOG = LoggerFactory.getLogger(EsReader.class);

    private EsReaderConfig esReaderConfig;

    private Map<String,Object> clientConfig;

    protected List<String> columnType;
    protected List<String> columnValue;
    protected List<String> columnName;

    @Override
    public DataStream<Row> read(FlinkExecuteContext executeContext) throws Exception {
        EsInputFormatBuilder builder = new EsInputFormatBuilder();
        builder.setColumnNames(columnName)
                .setColumnTypes(columnType)
                .setColumnValues(columnValue)
                .setAddress(esReaderConfig.getAddress())
                .setUsername(esReaderConfig.getUsername())
                .setPassword(esReaderConfig.getPassword())
                .setIndex(new String[]{esReaderConfig.getIndex()})
                .setType(new String[]{esReaderConfig.getIndexType()})
                .setBatchSize(esReaderConfig.getBatchSize())
                .setClientConfig(clientConfig)
                .setBytes(bytes)
                .setMonitorUrls(monitorUrls);
        if(esReaderConfig.getQuery() != null){
            builder.setQuery(new Gson().toJson(esReaderConfig.getQuery() ));
        }

        return createInput(executeContext, builder.finish());
    }

    @Override
    public void doInitialize(FlinkExecuteContext executeContext, EsReaderConfig config) {
        super.doInitialize(executeContext, config);
        this.esReaderConfig = config;

        clientConfig = new HashMap<>();
        clientConfig.put(EsReaderConfigKey.KEY_TIMEOUT.getName(), esReaderConfig.getTimeout());
        clientConfig.put(EsReaderConfigKey.KEY_PATH_PREFIX.getName(), esReaderConfig.getPathPrefix());

        List columns = ParameterUtils.getArrayList(config.getParameter(), EsReaderConfigKey.KEY_COLUMN);
        if(columns != null && columns.size() > 0) {
            if(columns.get(0) instanceof Map) {
                columnType = new ArrayList<>();
                columnValue = new ArrayList<>();
                columnName = new ArrayList<>();
                for(int i = 0; i < columns.size(); ++i) {
                    Map sm = (Map) columns.get(i);
                    columnType.add((String) sm.get("type"));
                    columnValue.add((String) sm.get("value"));
                    columnName.add((String) sm.get("name"));
                }

                LOG.info("init column finished");
            } else if (!JobCommonConstant.STAR_SYMBOL.equals(columns.get(0)) || columns.size() != 1) {
                throw new IllegalArgumentException("column argument error");
            }
        } else{
            throw new IllegalArgumentException("column argument error");
        }
    }

    @Override
    public String description() {
        return super.description();
    }
}
