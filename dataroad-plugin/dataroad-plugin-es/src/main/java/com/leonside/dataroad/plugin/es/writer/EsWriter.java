
package com.leonside.dataroad.plugin.es.writer;

import com.leonside.dataroad.common.spi.ItemWriter;
import com.leonside.dataroad.common.utils.ParameterUtils;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.flink.writer.BaseItemWriter;
import com.leonside.dataroad.plugin.es.config.EsWriterConfig;
import com.leonside.dataroad.plugin.es.config.EsWriterConfigKey;
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
public class EsWriter extends BaseItemWriter implements ItemWriter<FlinkExecuteContext, DataStream<Row>>, ComponentInitialization<FlinkExecuteContext, EsWriterConfig> {

    public static final int DEFAULT_BULK_ACTION = 100;

    public static final String DEFAULT_DOC = "_doc";

    private EsWriterConfig esWriterConfig;

    private Map<String,Object> clientConfig;
    private List<String> columnTypes;
    private List<String> columnNames;
    private List<String> idColumnNames;
    private List<String> idColumnTypes;
    private List<String> idColumnValues;

    @Override
    public void doInitialize(FlinkExecuteContext executeContext, EsWriterConfig config) {
        super.doInitialize(executeContext, config);
        this.esWriterConfig = (EsWriterConfig)config;

        clientConfig = new HashMap<>();
        clientConfig.put(EsWriterConfigKey.KEY_TIMEOUT.getName(), esWriterConfig.getTimeout());
        clientConfig.put(EsWriterConfigKey.KEY_PATH_PREFIX.getName(), esWriterConfig.getPathPrefix());

        List columns = ParameterUtils.getArrayList(esWriterConfig.getParameter(), EsWriterConfigKey.KEY_COLUMN);
        if(CollectionUtils.isNotEmpty(columns)) {
            columnTypes = new ArrayList<>();
            columnNames = new ArrayList<>();
            for(int i = 0; i < columns.size(); ++i) {
                Map sm = (Map) columns.get(i);
                columnNames.add((String) sm.get(EsWriterConfigKey.KEY_COLUMN_NAME));
                columnTypes.add((String) sm.get(EsWriterConfigKey.KEY_COLUMN_TYPE));
            }
        }

        List idColumns = (List) ParameterUtils.getArrayList(esWriterConfig.getParameter(), EsWriterConfigKey.KEY_ID_COLUMN);
        if( idColumns != null &&  idColumns.size() != 0) {
            idColumnNames = new ArrayList<>();
            idColumnTypes = new ArrayList<>();
            idColumnValues = new ArrayList<>();
            for(int i = 0; i <  idColumns.size(); ++i) {
                Map<String,Object> sm =
                        (Map)  idColumns.get(i);
                Object ind = sm.get(EsWriterConfigKey.KEY_ID_COLUMN_NAME);
                idColumnNames.add((String)ind);
                idColumnTypes.add((String) sm.get(EsWriterConfigKey.KEY_ID_COLUMN_TYPE));
                idColumnValues.add((String) sm.get(EsWriterConfigKey.KEY_ID_COLUMN_VALUE));
            }
        }
    }

    @Override
    public void write(FlinkExecuteContext executeContext, DataStream<Row> items) {
        EsOutputFormatBuilder builder = new EsOutputFormatBuilder();
        builder.setAddress(esWriterConfig.getAddress())
                .setUsername(esWriterConfig.getUsername())
                .setPassword(esWriterConfig.getPassword())
                .setIndex(esWriterConfig.getIndex())
                .setType(esWriterConfig.getIndexType())
                .setBatchInterval(esWriterConfig.getBulkAction())
                .setClientConfig(clientConfig)
                .setColumnNames(columnNames)
                .setColumnTypes(columnTypes)
                .setIdColumnIndices(idColumnNames)
                .setIdColumnTypes(idColumnTypes)
                .setIdColumnValues(idColumnValues)
                .setMonitorUrls(monitorUrls)
                .setErrors(errors);

            createOutput(items, builder.finish());
    }


}
