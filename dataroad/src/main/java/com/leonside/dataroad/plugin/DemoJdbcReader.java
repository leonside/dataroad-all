//package com.leonside.dataroad.plugin;
//
//import com.leonside.dataroad.common.spi.ItemReader;
//import com.leonside.dataroad.flink.context.FlinkExecuteContext;
//import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
//import org.apache.flink.api.common.typeinfo.TypeInformation;
//import org.apache.flink.api.java.typeutils.RowTypeInfo;
//import org.apache.flink.streaming.api.datastream.DataStream;
//import org.apache.flink.types.Row;
//
///**
// * @author leon
// */
//public class DemoJdbcReader implements ItemReader<FlinkExecuteContext, DataStream<Row>> {
//    @Override
//    public DataStream<Row> read(FlinkExecuteContext executeContext) throws Exception {
//        DataStream<Row> dataSource = executeContext.getEnvironment().createInput(new JdbcInputFormat.JdbcInputFormatBuilder()
//                .setDrivername("com.mysql.jdbc.Driver")
//                .setDBUrl("jdbc:mysql://10.254.10.31:3306/duceap_job_demo?useunicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai")
//                .setUsername("duceap")
//                .setPassword("123")
//                .setQuery("select id,name,sex,age,address,idcard,phone,code,create_time,area_code from student limit 100")
//                .setFetchSize(10)
//                .setRowTypeInfo(new RowTypeInfo(new TypeInformation[]{BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO,BasicTypeInfo.INT_TYPE_INFO,BasicTypeInfo.STRING_TYPE_INFO,
//                        BasicTypeInfo.STRING_TYPE_INFO,BasicTypeInfo.STRING_TYPE_INFO,BasicTypeInfo.INT_TYPE_INFO,BasicTypeInfo.DATE_TYPE_INFO,BasicTypeInfo.STRING_TYPE_INFO},
//                        new String[]{"id","name","sex","age","address","idcard","phone","code","create_time","area_code" }))
//                .finish()
//        );
//        return dataSource;
//    }
//
//    @Override
//    public String getName() {
//        return "reader1";
//    }
//}
