
package com.leonside.dataroad.plugin.es.writer;

import com.leonside.dataroad.common.exception.WriteRecordException;
import com.leonside.dataroad.common.utils.StringUtil;
import com.leonside.dataroad.flink.outputformat.GenericRichOutputFormat;
import com.leonside.dataroad.plugin.es.EsUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.types.Row;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * The OutputFormat class of ElasticSearch
 *
 */
public class EsOutputFormat extends GenericRichOutputFormat {

    protected String address;

    protected String username;

    protected String password;

    protected List<String> idColumnNames;

    protected List<String> idColumnValues;

    protected List<String> idColumnTypes;

    protected String index;

    protected String type;

    protected List<String> columnTypes;

    protected List<String> columnNames;

    protected Map<String,Object> clientConfig;

    private transient RestHighLevelClient client;

    private transient BulkRequest bulkRequest;


    @Override
    public void configure(Configuration configuration) {
        client = EsUtil.getClient(address, username, password, clientConfig);
        bulkRequest = new BulkRequest();
    }

    @Override
    public void doOpen(int taskNumber, int numTasks) throws IOException {

    }

    @Override
    protected void doWriteSingleRecord(Row row) throws WriteRecordException {
        String id = getId(row);
        IndexRequest request = StringUtils.isBlank(id) ? new IndexRequest(index, type) : new IndexRequest(index, type, id);
        request = request.source(EsUtil.rowToJsonMap(row, columnNames, columnTypes));
        try {
            client.index(request);
        } catch (Exception ex) {
            throw new WriteRecordException(ex.getMessage(), ex);
        }
    }

    @Override
    protected void doWriteMultipleRecords() throws Exception {
        bulkRequest = new BulkRequest();
        for(Row row : rows) {
            String id = getId(row);
            IndexRequest request = StringUtils.isBlank(id) ? new IndexRequest(index, type) : new IndexRequest(index, type, id);
            request = request.source(EsUtil.rowToJsonMap(row, columnNames, columnTypes));
            bulkRequest.add(request);
        }

        BulkResponse response = client.bulk(bulkRequest);
        if (response.hasFailures()){
            processFailResponse(response);
        }
    }

    private void processFailResponse(BulkResponse response){
        BulkItemResponse[] itemResponses = response.getItems();
        for (int i = 0; i < itemResponses.length; i++) {
            if(itemResponses[i].isFailed()){
                if (errCounter != null) {
                    errCounter.add(1);
                }
            }
        }
    }

    @Override
    public void doClose() throws IOException {
        if(client != null) {
            client.close();
        }
    }


    private String getId(Row record) throws WriteRecordException {
        if(idColumnNames == null || idColumnNames.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        try {
            for(; i < idColumnNames.size(); ++i) {
                String name = idColumnNames.get(i);
                String type =  idColumnTypes.get(i);
                sb.append(StringUtil.col2string(record.getField(name), type));
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            String msg = getClass().getName() + " Writing record error: when converting field[" + i + "] in Row(" + record + ")";
            throw new WriteRecordException(i,msg, ex);
        }

        return sb.toString();
    }

}
