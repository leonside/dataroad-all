package com.leonside.dataroad.plugin.oracle.converter;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.types.AtomicDataType;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.logical.LogicalTypeRoot;

import java.util.Locale;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class OracleRawTypeConverter {

    private static final String TIMESTAMP = "^TIMESTAMP\\(\\d+\\)";
    private static final Predicate<String> TIMESTAMP_PREDICATE =
            Pattern.compile(TIMESTAMP).asPredicate();

    /**
     * 将Oracle数据库中的类型，转换成flink的DataType类型。
     *
     * @param type
     * @return
     */
    public static DataType apply(String type) {
        switch (type.toUpperCase(Locale.ENGLISH)) {
            case "SMALLINT":
                return DataTypes.SMALLINT();
            case "BINARY_DOUBLE":
                return DataTypes.DOUBLE();
            case "CHAR":
            case "VARCHAR":
            case "VARCHAR2":
            case "NCHAR":
            case "NVARCHAR2":
                return DataTypes.STRING();
            case "CLOB":
            case "NCLOB":
                return new AtomicDataType(new ClobType(true, LogicalTypeRoot.VARCHAR));
                //            case "XMLTYPE":
            case "INT":
            case "INTEGER":
            case "NUMBER":
            case "DECIMAL":
            case "FLOAT":
                return DataTypes.DECIMAL(38, 18);
            case "DATE":
                return DataTypes.DATE();
            case "RAW":
            case "LONG RAW":
                return DataTypes.BYTES();
            case "BLOB":
                return new AtomicDataType(new BlobType(true, LogicalTypeRoot.VARBINARY));
            case "BINARY_FLOAT":
                return DataTypes.FLOAT();
            case "LONG":
                // when mode is update and allReplace is false, LONG type is not support
            default:
                if (TIMESTAMP_PREDICATE.test(type)) {
                    return DataTypes.TIMESTAMP();
                } else if (type.startsWith("INTERVAL")) {
                    return DataTypes.STRING();
                }
                throw new UnsupportedOperationException("unsupport type [" + type +"]");
        }
    }
}