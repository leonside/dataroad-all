package com.leonside.dataroad.flink.utils;

import com.leonside.dataroad.common.domain.MetaColumn;
import com.leonside.dataroad.common.exception.JobConfigException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.types.utils.TypeConversions;

import java.util.List;

/**
 * @author leon
 */
public class RawTypeUtils {

    public static TypeInformation createRowTypeInfo(RawTypeConverter converter, List<MetaColumn> metaColumns) {
        if(CollectionUtils.isEmpty(metaColumns) || StringUtils.isEmpty(metaColumns.get(0).getType())){
            throw new JobConfigException("column and type can not be null");
        }

        DataType[] types = metaColumns.stream().map(it -> converter.apply(it.getType())).toArray(DataType[]::new);
        String[] names = metaColumns.stream().map(it -> it.getName()).toArray(String[]::new);

        return new RowTypeInfo(TypeConversions.fromDataTypeToLegacyInfo(types), names);
    }

}
