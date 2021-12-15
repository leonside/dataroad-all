
package com.leonside.dataroad.plugin.es.reader;

import com.google.gson.Gson;
import com.leonside.dataroad.common.constant.JobCommonConstant;
import com.leonside.dataroad.common.spi.ItemReader;
import com.leonside.dataroad.common.utils.ParameterUtils;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.flink.reader.BaseItemReader;
import com.leonside.dataroad.plugin.es.EsReaderKey;
import com.leonside.dataroad.plugin.es.EsWriterKey;
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
public class EsReader extends BaseItemReader implements ItemReader<FlinkExecuteContext, DataStream<Row>> {

    private static Logger LOG = LoggerFactory.getLogger(EsReader.class);

    private String address;
    private String username;
    private String password;
    private String query;

    private String[] index;
    private String[] type;
    private Integer batchSize;
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
                .setAddress(address)
                .setUsername(username)
                .setPassword(password)
                .setIndex(index)
                .setType(type)
                .setBatchSize(batchSize)
                .setClientConfig(clientConfig)
                .setQuery(query)
                .setBytes(bytes)
                .setMonitorUrls(monitorUrls);
//        builder.setTestConfig(testConfig);
//        builder.setLogConfig(logConfig);

        return createInput(executeContext, builder.finish());
    }

    @Override
    public void initialize(FlinkExecuteContext executeContext, Map<String, Object> parameter) {
        super.initialize(executeContext, parameter);
        address = ParameterUtils.getString(parameter,EsReaderKey.KEY_ADDRESS);
        username = ParameterUtils.getString(parameter,EsReaderKey.KEY_USERNAME);
        password = ParameterUtils.getString(parameter,EsReaderKey.KEY_PASSWORD);
        index = ParameterUtils.getArrayList(parameter,EsReaderKey.KEY_INDEX).toArray(new String[]{});
        type = ParameterUtils.getArrayList(parameter,EsReaderKey.KEY_TYPE).toArray(new String[]{});
        batchSize = ParameterUtils.getInteger(parameter,EsReaderKey.KEY_BATCH_SIZE);

        clientConfig = new HashMap<>();
        clientConfig.put(EsReaderKey.KEY_TIMEOUT.getName(), ParameterUtils.getInteger(parameter,EsReaderKey.KEY_TIMEOUT));
        clientConfig.put(EsReaderKey.KEY_PATH_PREFIX.getName(), ParameterUtils.getString(parameter,EsReaderKey.KEY_PATH_PREFIX));

        Object queryMap = parameter.get(EsReaderKey.KEY_QUERY.getName());
        if(queryMap != null) {
            query = new Gson().toJson(queryMap);
        }
        List columns = ParameterUtils.getArrayList(parameter, EsWriterKey.KEY_COLUMN);
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
}
