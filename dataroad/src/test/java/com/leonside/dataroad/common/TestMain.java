package com.leonside.dataroad.common;

import org.apache.flink.types.Row;

/**
 * @author leon
 */
public class TestMain {
    public static void main(String[] args) {
        Row row = Row.withNames();
        row.setField("name1","zhangs");
        row.setField("name2","lisi");

        Row row2 = Row.withNames();
        row2.setField("name3","zhangs2");
        row2.setField("name1","lisi2");

        Row join = row.copy(row2);
        System.out.println(join);
    }
}
