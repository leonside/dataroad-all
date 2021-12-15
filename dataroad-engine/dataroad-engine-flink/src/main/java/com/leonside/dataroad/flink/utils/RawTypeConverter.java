package com.leonside.dataroad.flink.utils;

import org.apache.flink.table.types.DataType;

/**
 * Each connector implements. It is used to convert raw type to flink type.
 *
 * <p>e.g.: convert string "SHORT" to {@link DataType}.
 */
@FunctionalInterface
public interface RawTypeConverter {

    /**
     * @param type raw type string. e.g.: "SHORT", "INT", "TIMESTAMP"
     * @return e.g.: DataTypes.INT(), DataTypes.TIMESTAMP().
     */
    DataType apply(String type);
}
