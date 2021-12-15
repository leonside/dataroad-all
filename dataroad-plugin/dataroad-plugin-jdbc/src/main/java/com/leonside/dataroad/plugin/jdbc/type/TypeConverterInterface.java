
package com.leonside.dataroad.plugin.jdbc.type;

import java.io.Serializable;

/**
 * Data type converter
 *
 */
public interface TypeConverterInterface extends Serializable {

    /**
     * 类型转换，将数据库数据某类型的对象转换为对应的Java基本数据对象实例
     * @param data      数据记录
     * @param typeName  数据类型
     * @return
     */
    Object convert(Object data,String typeName);

}
