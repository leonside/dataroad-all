package com.leonside.dataroad.plugin.postgresql;

import com.leonside.dataroad.plugin.jdbc.type.TypeConverterInterface;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class PostgresqlTypeConverter implements TypeConverterInterface {

    private List<String> stringTypes = Arrays.asList("uuid","xml","cidr","inet","macaddr");

    private List<String> byteTypes = Arrays.asList("bytea","bit varying");

    private List<String> bitTypes = Collections.singletonList("bit");

    private List<String> doubleTypes = Collections.singletonList("money");

    private List<String> intTypes = Arrays.asList("int","int2","int4","int8");

    protected static List<String> STRING_TYPES = Arrays.asList("CHAR", "VARCHAR","TINYBLOB","TINYTEXT","BLOB","TEXT", "MEDIUMBLOB", "MEDIUMTEXT", "LONGBLOB", "LONGTEXT");


    @Override
    public Object convert(Object data,String typeName) {
        if (data == null){
            return null;
        }
        String dataValue = data.toString();
        if(stringTypes.contains(typeName)){
            return dataValue;
        }
        if(StringUtils.isBlank(dataValue)){
            //如果是string类型 还应该返回空字符串而不是null
            if(STRING_TYPES.contains(typeName.toUpperCase(Locale.ENGLISH))){
                return dataValue;
            }
            return null;
        }
        if(doubleTypes.contains(typeName)){
            if(StringUtils.startsWith(dataValue, "$")){
                dataValue = StringUtils.substring(dataValue, 1);
            }
            data = Double.parseDouble(dataValue);
        } else if(bitTypes.contains(typeName)){
            //
        }else if(byteTypes.contains(typeName)){
            data = Byte.valueOf(dataValue);
        } else if(intTypes.contains(typeName)){
            if(dataValue.contains(".")){
                dataValue =  new BigDecimal(dataValue).stripTrailingZeros().toPlainString();
            }
            data = Long.parseLong(dataValue);
        }

        return data;
    }
}