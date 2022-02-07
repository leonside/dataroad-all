package com.leonside.dataroad.plugin.postgresql;

import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.types.DataType;

import java.util.Locale;

public class PostgresqlRawTypeConverter {

    /**
     * inspired by Postgresql doc. https://www.postgresql.org/docs/current/datatype.html
     *
     * @param type
     */
//    public static DataType apply(String type) {
//        switch (type.toUpperCase(Locale.ENGLISH)) {
//                // Numeric Types
//            case "SMALLINT":
//            case "SMALLSERIAL":
//            case "INT2":
//                return DataTypes.SMALLINT();
//            case "INT":
//            case "INTEGER":
//            case "SERIAL":
//            case "INT4":
//                return DataTypes.INT();
//            case "BIGINT":
//            case "BIGSERIAL":
//            case "OID":
//            case "INT8":
//                return DataTypes.BIGINT();
//            case "REAL":
//            case "FLOAT4":
//                return DataTypes.FLOAT();
//            case "FLOAT":
//            case "DOUBLE PRECISION":
//            case "FLOAT8":
//                return DataTypes.DOUBLE();
//            case "DECIMAL":
//            case "NUMERIC":
//                //            case "MONEY":
//                return DataTypes.DECIMAL(38, 18);
//
//                // Character Types
//            case "CHARACTER VARYING":
//            case "VARCHAR":
//            case "CHARACTER":
//            case "CHAR":
//            case "TEXT":
//            case "NAME":
//            case "BPCHAR":
//                return DataTypes.STRING();
//
//                // Binary Data Types
//            case "BYTEA":
//                return DataTypes.BYTES();
//
//                // Date/Time Types
//            case "TIMESTAMP":
//            case "TIMESTAMPTZ":
//                return DataTypes.TIMESTAMP();
//            case "DATE":
//                return DataTypes.DATE();
//            case "TIME":
//            case "TIMETZ":
//                return DataTypes.TIME();
//                // interval 类型还不知道如何支持
//
//                // Boolean Type
//            case "BOOLEAN":
//            case "BOOL":
//                return DataTypes.BOOLEAN();
//
//                // 以下类型无法支持
//                // Enumerated Types
//
//                // Geometric Types
//                //            case "POINT":
//                //            case "LINE":
//                //            case "LSEG":
//                //            case "BOX":
//                //            case "PATH":
//                //            case "POLYGON":
//                //            case "CIRCLE":
//
//                // Network Address Types
//
//                // Bit String Types
//                //            case "BIT":
//                //                return DataTypes.BOOLEAN();
//                //            case "BIT VARYING":
//                //                return DataTypes.STRING();
//                //
//                //            case "XML":
//                //                return DataTypes.STRING();
//                //
//                //                // JSON Types
//                //            case "JSON":
//                //            case "JSONB":
//                //            case "JSONPATH":
//                //                return DataTypes.STRING();
//
//            default:
//                throw new UnsupportedOperationException("unsupport type [" + type +"]");
//        }
//    }


    public static TypeInformation apply(String type) {
        switch (type.toUpperCase(Locale.ENGLISH)) {
            // Numeric Types
            case "SMALLINT":
            case "SMALLSERIAL":
            case "INT2":
                return Types.INT;
            case "INT":
            case "INTEGER":
            case "SERIAL":
            case "INT4":
                return BasicTypeInfo.INT_TYPE_INFO;
            case "BIGINT":
            case "BIGSERIAL":
            case "OID":
            case "INT8":
                return BasicTypeInfo.BIG_INT_TYPE_INFO;
            case "REAL":
            case "FLOAT4":
                return BasicTypeInfo.FLOAT_TYPE_INFO;
            case "FLOAT":
            case "DOUBLE PRECISION":
            case "FLOAT8":
                return BasicTypeInfo.DOUBLE_TYPE_INFO;
            case "DECIMAL":
            case "NUMERIC":
                //            case "MONEY":
                return BasicTypeInfo.BIG_DEC_TYPE_INFO;

            // Character Types
            case "CHARACTER VARYING":
            case "VARCHAR":
            case "CHARACTER":
            case "CHAR":
            case "TEXT":
            case "NAME":
            case "BPCHAR":
                return BasicTypeInfo.STRING_TYPE_INFO;

            // Binary Data Types
            case "BYTEA":
                return BasicTypeInfo.BYTE_TYPE_INFO;

            // Date/Time Types
            case "TIMESTAMP":
            case "TIMESTAMPTZ":
                return Types.SQL_TIMESTAMP;
            case "DATE":
                return BasicTypeInfo.DATE_TYPE_INFO;
            case "TIME":
            case "TIMETZ":
                return Types.SQL_TIME;
            // interval 类型还不知道如何支持

            // Boolean Type
            case "BOOLEAN":
            case "BOOL":
                return BasicTypeInfo.BOOLEAN_TYPE_INFO;

            // 以下类型无法支持
            // Enumerated Types

            // Geometric Types
            //            case "POINT":
            //            case "LINE":
            //            case "LSEG":
            //            case "BOX":
            //            case "PATH":
            //            case "POLYGON":
            //            case "CIRCLE":

            // Network Address Types

            // Bit String Types
            //            case "BIT":
            //                return DataTypes.BOOLEAN();
            //            case "BIT VARYING":
            //                return DataTypes.STRING();
            //
            //            case "XML":
            //                return DataTypes.STRING();
            //
            //                // JSON Types
            //            case "JSON":
            //            case "JSONB":
            //            case "JSONPATH":
            //                return DataTypes.STRING();

            default:
                throw new UnsupportedOperationException("unsupport type [" + type +"]");
        }
    }
}