
package com.leonside.dataroad.plugin.es.writer;


import com.leonside.dataroad.flink.outputformat.GenericRichOutputFormatBuilder;

import java.util.List;
import java.util.Map;

/**
 * The Builder class of EsOutputFormat
 *
 */
public class EsOutputFormatBuilder extends GenericRichOutputFormatBuilder<EsOutputFormat, EsOutputFormatBuilder> {

    private EsOutputFormat format;

    public EsOutputFormatBuilder() {
        super.format = format = new EsOutputFormat();
    }

    public EsOutputFormatBuilder setAddress(String address) {
        format.address = address;
        return this;
    }


    public EsOutputFormatBuilder setUsername(String username) {
        format.username = username;
        return this;
    }

    public EsOutputFormatBuilder setPassword(String password) {
        format.password = password;
        return this;
    }

    public EsOutputFormatBuilder setIdColumnIndices(List<String> idColumnNames) {
        format.idColumnNames = idColumnNames;
        return this;
    }

    public EsOutputFormatBuilder setIdColumnTypes(List<String> idColumnTypes) {
        format.idColumnTypes = idColumnTypes;
        return this;
    }

    public EsOutputFormatBuilder setIdColumnValues(List<String> idColumnValues) {
        format.idColumnValues = idColumnValues;
        return this;
    }

    public EsOutputFormatBuilder setType(String type) {
        format.type = type;
        return this;
    }

    public EsOutputFormatBuilder setIndex(String index) {
        format.index = index;
        return this;
    }

    public EsOutputFormatBuilder setColumnNames(List<String> columnNames) {
        format.columnNames = columnNames;
        return this;
    }

    public EsOutputFormatBuilder setColumnTypes(List<String> columnTypes) {
        format.columnTypes = columnTypes;
        return this;
    }

    public EsOutputFormatBuilder setClientConfig(Map<String, Object> clientConfig){
        format.clientConfig = clientConfig;
        return this;
    }

    @Override
    protected void checkFormat() {
        if (format.getRestoreConfig() != null && format.getRestoreConfig().isRestore()){
            throw new UnsupportedOperationException("This plugin not support restore from failed state");
        }
    }
}
