package com.leonside.dataroad.dashboard.utils;

import com.leonside.dataroad.dashboard.domian.JobFlowConfig;

import java.util.List;

/**
 * @author leon
 */
public class PageUtils {
    public static <T> List<T> subList(List<T> dataList, int page, int limit) {

        int begin =  (page - 1) * limit;
        int end = (begin + limit) > dataList.size() ? dataList.size()  : begin + limit;
        return dataList.subList(begin, end);
    }
}
