//package com.leonside.dataroad.plugin.jdbc.lookup;
//
//import org.apache.flink.configuration.Configuration;
//import org.apache.flink.streaming.api.functions.async.ResultFuture;
//import org.apache.flink.types.Row;
//
///**
// * @author leon
// */
//public abstract class JdbcLruLookupFunction extends AbstractLruLookupFunction {
//
//    @Override
//    public void open(Configuration parameters) throws Exception {
//        super.open(parameters);
////        Class.forName(driverName);
////        conn = DriverManager.getConnection(jdbcUrl, username, password);
////        ps = conn.prepareStatement("select value from dm_sys_code where code_id='dm_islazydata' and code = ?");
//    }
//
//    @Override
//    public void timeout(Row input, ResultFuture<Row> resultFuture) throws Exception {
//        super.timeout(input, resultFuture);
////        ArrayList<Row> rows = Lists.newArrayList(input);
////        resultFuture.complete(rows);
//    }
//
//    @Override
//    public void asyncInvoke(Row row, ResultFuture<Row> resultFuture) throws Exception {
//        loadData(row, resultFuture);
//    }
//
//    @Override
//    public void close() throws Exception {
//        super.close();
////        conn.close();
//    }
//}
