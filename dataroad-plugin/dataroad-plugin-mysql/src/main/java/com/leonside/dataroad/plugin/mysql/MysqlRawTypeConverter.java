package com.leonside.dataroad.plugin.mysql;

import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.types.DataType;

import java.util.Locale;

/**
 */
public class MysqlRawTypeConverter {

//    /**
//     * 将MySQL数据库中的类型，转换成flink的DataType类型。 转换关系参考 com.mysql.jdbc.MysqlDefs 类里面的信息。
//     * com.mysql.jdbc.ResultSetImpl.getObject(int)
//     */
//    public static DataType apply(String type) {
//        switch (type.toUpperCase(Locale.ENGLISH)) {
//            case "BOOLEAN":
//            case "BIT":
//                return DataTypes.BOOLEAN();
//            case "TINYINT":
//                return DataTypes.TINYINT();
//            case "SMALLINT":
//            case "MEDIUMINT":
//            case "INT":
//            case "INTEGER":
//            case "INT24":
//                return DataTypes.INT();
//            case "BIGINT":
//                return DataTypes.BIGINT();
//            case "REAL":
//            case "FLOAT":
//                return DataTypes.FLOAT();
//            case "DECIMAL":
//            case "NUMERIC":
//                return DataTypes.DECIMAL(38, 18);
//            case "DOUBLE":
//                return DataTypes.DOUBLE();
//            case "CHAR":
//            case "VARCHAR":
//            case "STRING":
//                return DataTypes.STRING();
//            case "DATE":
//                return DataTypes.DATE();
//            case "TIME":
//                return DataTypes.TIME();
//            case "YEAR":
//                return DataTypes.INTERVAL(DataTypes.YEAR());
//            case "TIMESTAMP":
//            case "DATETIME":
//                return DataTypes.TIMESTAMP();
//            case "TINYBLOB":
//            case "BLOB":
//            case "MEDIUMBLOB":
//            case "LONGBLOB":
//                return DataTypes.BYTES();
//            case "TINYTEXT":
//            case "TEXT":
//            case "MEDIUMTEXT":
//            case "LONGTEXT":
//                return DataTypes.STRING();
//            case "BINARY":
//            case "VARBINARY":
//                // BYTES 底层调用的是VARBINARY最大长度
//                return DataTypes.BYTES();
//            case "JSON":
//                return DataTypes.STRING();
//            case "ENUM":
//            case "SET":
//                return DataTypes.STRING();
//            case "GEOMETRY":
//                return DataTypes.BYTES();
//
//            default:
//                throw new UnsupportedOperationException("unsupport type [" + type +"]");
//        }
//    }

    public static TypeInformation apply(String type) {
        switch (type.toUpperCase(Locale.ENGLISH)) {
            case "BOOLEAN":
            case "BIT":
                return Types.BOOLEAN;
            case "TINYINT":
                return Types.SHORT;
            case "SMALLINT":
            case "MEDIUMINT":
            case "INT":
            case "INTEGER":
            case "INT24":
                return Types.INT;
            case "BIGINT":
                return Types.BIG_INT;
            case "REAL":
            case "FLOAT":
                return Types.FLOAT;
            case "DECIMAL":
            case "NUMERIC":
                return Types.BIG_DEC;
            case "DOUBLE":
                return Types.DOUBLE;
            case "CHAR":
            case "VARCHAR":
            case "STRING":
                return Types.STRING;
            case "DATE":
                return BasicTypeInfo.DATE_TYPE_INFO;
            case "TIME":
                return Types.SQL_TIME;  //todo
            case "YEAR":
                return BasicTypeInfo.DATE_TYPE_INFO; //todo
            case "TIMESTAMP":
                return Types.SQL_TIMESTAMP;
            case "DATETIME":
                return BasicTypeInfo.DATE_TYPE_INFO;
            case "TINYBLOB":
            case "BLOB":
            case "MEDIUMBLOB":
            case "LONGBLOB":
                return BasicTypeInfo.BYTE_TYPE_INFO;
            case "TINYTEXT":
            case "TEXT":
            case "MEDIUMTEXT":
            case "LONGTEXT":
                return Types.STRING;
            case "BINARY":
            case "VARBINARY":
                // BYTES 底层调用的是VARBINARY最大长度
                return BasicTypeInfo.BYTE_TYPE_INFO;
            case "JSON":
                return BasicTypeInfo.STRING_TYPE_INFO;
            case "ENUM":
            case "SET":
                return BasicTypeInfo.STRING_TYPE_INFO;
            case "GEOMETRY":
                return BasicTypeInfo.BYTE_TYPE_INFO;

            default:
                throw new UnsupportedOperationException("unsupport type [" + type +"]");
        }
    }
}