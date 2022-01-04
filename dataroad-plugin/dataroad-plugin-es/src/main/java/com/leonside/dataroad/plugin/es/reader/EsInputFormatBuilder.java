
package com.leonside.dataroad.plugin.es.reader;

import com.leonside.dataroad.common.constant.JobCommonConstant;
import com.leonside.dataroad.flink.reader.inputformat.GenericRichInputFormatBuilder;

import java.util.List;
import java.util.Map;

/**
 * The builder class of EsInputFormat
 *
 */
public class EsInputFormatBuilder extends GenericRichInputFormatBuilder {

    private EsInputFormat format;

    public EsInputFormatBuilder() {
        super.format = format = new EsInputFormat();
    }

    public EsInputFormatBuilder setAddress(String address) {
        format.address = address;
        return this;
    }

    public EsInputFormatBuilder setUsername(String username) {
        format.username = username;
        return this;
    }

    public EsInputFormatBuilder setPassword(String password) {
        format.password = password;
        return this;
    }

    public EsInputFormatBuilder setQuery(String query) {
        format.query = query;
        return this;
    }

    public EsInputFormatBuilder setColumnNames(String query) {
        format.query = query;
        return this;
    }

    public EsInputFormatBuilder setColumnNames(List<String> columnNames) {
        format.columnNames = columnNames;
        return this;
    }

    public EsInputFormatBuilder setColumnValues(List<String> columnValues) {
        format.columnValues = columnValues;
        return this;
    }

    public EsInputFormatBuilder setColumnTypes(List<String> columnTypes) {
        format.columnTypes = columnTypes;
        return this;
    }

    public EsInputFormatBuilder setIndex(String[] index){
        format.index = index;
        return this;
    }

    public EsInputFormatBuilder setType(String[] type){
        format.type = type;
        return this;
    }

    public EsInputFormatBuilder setBatchSize(Integer batchSize){
        if(batchSize != null && batchSize > 0){
            format.batchSize = batchSize;
        }
        return this;
    }

    public EsInputFormatBuilder setClientConfig(Map<String, Object> clientConfig){
        format.clientConfig = clientConfig;
        return this;
    }

    @Override
    public boolean validate() {
        if (format.getRestoreConfig() != null && format.getRestoreConfig().isRestore()){
            throw new UnsupportedOperationException("This plugin not support restore from failed state");
        }

        if (format.batchSize > JobCommonConstant.MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("批量读取数量不能大于[200000]条");
        }

        return true;
    }
}
