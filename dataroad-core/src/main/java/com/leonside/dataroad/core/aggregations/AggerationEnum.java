package com.leonside.dataroad.core.aggregations;

import com.leonside.dataroad.common.utils.Asserts;

import java.util.List;
import java.util.stream.Collectors;

public enum AggerationEnum {
        AVG,
        SUM,
        COUNT,
        MAX,
        MIN,
        STATS,
        TOPHITS,
        TOTALHITS,
        MULTI;

        public static List<AggerationEnum> of(List<String> aggNames){
                Asserts.notEmpty(aggNames, "aggNames can not be null");
                return aggNames.stream().map(it->AggerationEnum.valueOf(it.toUpperCase())).collect(Collectors.toList());
        }
}