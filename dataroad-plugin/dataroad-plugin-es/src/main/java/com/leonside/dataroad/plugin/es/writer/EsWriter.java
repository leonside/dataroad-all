
package com.leonside.dataroad.plugin.es.writer;

import com.leonside.dataroad.common.spi.ItemWriter;
import com.leonside.dataroad.common.utils.ParameterUtils;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.flink.writer.BaseItemWriter;
import com.leonside.dataroad.plugin.es.EsWriterKey;
import org.apache.commons.collections.CollectionUtils;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The writer plugin of ElasticSearch
 *
 */
public class EsWriter extends BaseItemWriter implements ItemWriter<FlinkExecuteContext, DataStream<Row>> {

    public static final int DEFAULT_BULK_ACTION = 100;

    public static final String DEFAULT_DOC = "_doc";

    private String address;
    private String username;
    private String password;
    private String index;
    private String type;
    private int bulkAction;
    private Map<String,Object> clientConfig;
    private List<String> columnTypes;
    private List<String> columnNames;
    private List<String> idColumnNames;
    private List<String> idColumnTypes;
    private List<String> idColumnValues;

    @Override
    public void initialize(FlinkExecuteContext executeContext, Map<String, Object> parameter) {
        super.initialize(executeContext, parameter);

        address = ParameterUtils.getString(parameter, EsWriterKey.KEY_ADDRESS);
        username = ParameterUtils.getString(parameter, EsWriterKey.KEY_USERNAME);
        password = ParameterUtils.getString(parameter, EsWriterKey.KEY_PASSWORD);
        type = ParameterUtils.getString(parameter, EsWriterKey.KEY_TYPE);
        index = ParameterUtils.getString(parameter, EsWriterKey.KEY_INDEX);
        bulkAction = ParameterUtils.getInteger(parameter, EsWriterKey.KEY_BULK_ACTION);

        clientConfig = new HashMap<>();
        clientConfig.put(EsWriterKey.KEY_TIMEOUT.getName(), ParameterUtils.getInteger(parameter, EsWriterKey.KEY_TIMEOUT));
        clientConfig.put(EsWriterKey.KEY_PATH_PREFIX.getName(), ParameterUtils.getString(parameter, EsWriterKey.KEY_PATH_PREFIX));

        List columns = ParameterUtils.getArrayList(parameter, EsWriterKey.KEY_COLUMN);
        if(CollectionUtils.isNotEmpty(columns)) {
            columnTypes = new ArrayList<>();
            columnNames = new ArrayList<>();
            for(int i = 0; i < columns.size(); ++i) {
                Map sm = (Map) columns.get(i);
                columnNames.add((String) sm.get(EsWriterKey.KEY_COLUMN_NAME));
                columnTypes.add((String) sm.get(EsWriterKey.KEY_COLUMN_TYPE));
            }
        }

        List idColumns = (List) ParameterUtils.getArrayList(parameter, EsWriterKey.KEY_ID_COLUMN);
        if( idColumns != null &&  idColumns.size() != 0) {
            idColumnNames = new ArrayList<>();
            idColumnTypes = new ArrayList<>();
            idColumnValues = new ArrayList<>();
            for(int i = 0; i <  idColumns.size(); ++i) {
                Map<String,Object> sm =
                        (Map)  idColumns.get(i);
                Object ind = sm.get(EsWriterKey.KEY_ID_COLUMN_NAME);
                idColumnNames.add((String)ind);
                idColumnTypes.add((String) sm.get(EsWriterKey.KEY_ID_COLUMN_TYPE));
                idColumnValues.add((String) sm.get(EsWriterKey.KEY_ID_COLUMN_VALUE));
            }
        }

    }

    @Override
    public void write(FlinkExecuteContext executeContext, DataStream<Row> items) {
        EsOutputFormatBuilder builder = new EsOutputFormatBuilder();
        builder.setAddress(address)
                .setUsername(username)
                .setPassword(password)
                .setIndex(index)
                .setType(type)
                .setBatchInterval(bulkAction)
                .setClientConfig(clientConfig)
                .setColumnNames(columnNames)
                .setColumnTypes(columnTypes)
                .setIdColumnIndices(idColumnNames)
                .setIdColumnTypes(idColumnTypes)
                .setIdColumnValues(idColumnValues)
                .setMonitorUrls(monitorUrls)
                .setErrors(errors);
//        builder.setSrcCols(srcCols);

            createOutput(items, builder.finish());
    }
}
