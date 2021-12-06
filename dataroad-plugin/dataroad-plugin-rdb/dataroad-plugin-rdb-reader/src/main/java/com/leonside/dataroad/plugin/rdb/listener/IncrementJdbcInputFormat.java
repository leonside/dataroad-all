//package com.leonside.dataroad.plugin.rdb;
//
//import com.leonside.dataroad.common.constant.JobCommonConstant;
//import com.leonside.dataroad.common.domain.ColumnType;
//import com.leonside.dataroad.common.utils.ExceptionUtil;
//import com.leonside.dataroad.common.utils.JsonUtil;
//import com.leonside.dataroad.common.utils.StringUtil;
//import com.leonside.dataroad.flink.metric.Metrics;
//import com.leonside.dataroad.flink.utils.UrlUtil;
//import com.leonside.dataroad.plugin.rdb.inputformat.BigIntegerMaximum;
//import com.leonside.dataroad.plugin.rdb.inputformat.JdbcInputSplit;
//import com.leonside.dataroad.plugin.rdb.inputformat.StringAccumulator;
//import com.leonside.dataroad.plugin.rdb.inputformat.IncrementConfig;
//import com.leonside.dataroad.plugin.rdb.utils.DbUtil;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.flink.core.io.InputSplit;
//
//import java.io.IOException;
//import java.math.BigInteger;
//import java.sql.*;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
///**
// * @author leon
// */
//public class IncrementJdbcInputFormat extends GenericJdbcInputFormat2 {
//
//    public IncrementConfig incrementConfig;
//    //轮询增量标识字段类型
//    public ColumnType type;
//
//
//    @Override
//    protected InputSplit[] doCreateInputSplits(int minNumSplits) throws Exception {
//        JdbcInputSplit[] splits = new JdbcInputSplit[minNumSplits];
//        for (int i = 0; i < minNumSplits; i++) {
//            splits[i] = new JdbcInputSplit(i, numPartitions, i, incrementConfig.getStartLocation(), null);
//        }
//
//        return splits;
//    }
//
//    /**
//     * 初始化增量或或间隔轮询任务累加器
//     *
//     * @param inputSplit 数据分片
//     */
//    @Override
//    protected void initAccumulator(InputSplit inputSplit) {
//        //初始化增量、轮询字段类型
//        type = ColumnType.fromString(incrementConfig.getColumnType());
//        startLocationAccumulator = new BigIntegerMaximum();
//        endLocationAccumulator = new BigIntegerMaximum();
//        String startLocation = StringUtil.stringToTimestampStr(incrementConfig.getStartLocation(), type);
//
//
//        if (StringUtils.isNotEmpty(incrementConfig.getStartLocation())) {
//            ((JdbcInputSplit) inputSplit).setStartLocation(startLocation);
//            startLocationAccumulator.add(new BigInteger(startLocation));
//        }
//
//        //轮询任务endLocation设置为startLocation的值
//        if (incrementConfig.isPolling()) {
//            if (StringUtils.isNotEmpty(startLocation)) {
//                endLocationAccumulator.add(new BigInteger(startLocation));
//            }
//        } else if (incrementConfig.isUseMaxFunc()) {
//            //如果不是轮询任务，则只能是增量任务，若useMaxFunc设置为true，则去数据库查询当前增量字段的最大值
//            getMaxValue(inputSplit);
//            //endLocation设置为数据库中查询的最大值
//            String endLocation = ((JdbcInputSplit) inputSplit).getEndLocation();
//            endLocationAccumulator.add(new BigInteger(StringUtil.stringToTimestampStr(endLocation, type)));
//        }else{
//            //增量任务，且useMaxFunc设置为false，如果startLocation不为空，则将endLocation初始值设置为startLocation的值，防止数据库无增量数据时下次获取到的startLocation为空
//            if (StringUtils.isNotEmpty(startLocation)) {
//                endLocationAccumulator.add(new BigInteger(startLocation));
//            }
//        }
//
//        //将累加器信息添加至prometheus
////        customPrometheusReporter.registerMetric(startLocationAccumulator, Metrics.START_LOCATION);
////        customPrometheusReporter.registerMetric(endLocationAccumulator, Metrics.END_LOCATION);
//        getRuntimeContext().addAccumulator(Metrics.START_LOCATION, startLocationAccumulator);
//        getRuntimeContext().addAccumulator(Metrics.END_LOCATION, endLocationAccumulator);
//    }
//
//    /**
//     * 将增量任务的数据最大值设置到累加器中
//     *
//     * @param inputSplit 数据分片
//     */
//    protected void getMaxValue(InputSplit inputSplit) {
//        String maxValue;
//        if (inputSplit.getSplitNumber() == 0) {
//            maxValue = getMaxValueFromDb();
//            //将累加器信息上传至flink，供其他通道通过flink rest api获取该最大值
//            maxValueAccumulator = new StringAccumulator();
//            maxValueAccumulator.add(maxValue);
//            getRuntimeContext().addAccumulator(Metrics.MAX_VALUE, maxValueAccumulator);
//        } else {
//            maxValue = getMaxValueFromApi();
//        }
//
//        if (StringUtils.isEmpty(maxValue)) {
//            throw new RuntimeException("Can't get the max value from accumulator");
//        }
//
//        ((JdbcInputSplit) inputSplit).setEndLocation(maxValue);
//    }
//
//    /**
//     * 从flink rest api中获取累加器最大值
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//    public String getMaxValueFromApi(){
//        if(StringUtils.isEmpty(monitorUrls)){
//            return null;
//        }
//
//        String url = monitorUrls;
//        if (monitorUrls.startsWith(JobCommonConstant.PROTOCOL_HTTP)) {
//            url = String.format("%s/jobs/%s/accumulators", monitorUrls, jobId);
//        }
//
//        //The extra 10 times is to ensure that accumulator is updated
//        int maxAcquireTimes = (queryTimeOut / incrementConfig.getRequestAccumulatorInterval()) + 10;
//
//        final String[] maxValue = new String[1];
//        UrlUtil.get(url, incrementConfig.getRequestAccumulatorInterval() * 1000, maxAcquireTimes, new UrlUtil.Callback() {
//
//            @Override
//            public void call(String response) {
//                Map map = JsonUtil.getInstance().readJson(response, Map.class);
//
//                LOG.info("Accumulator data:" + JsonUtil.getInstance().writeJson(map));
//
//                List<Map> userTaskAccumulators = (List<Map>) map.get("user-task-accumulators");
//                for (Map accumulator : userTaskAccumulators) {
//                    if (Metrics.MAX_VALUE.equals(accumulator.get("name"))) {
//                        maxValue[0] = (String) accumulator.get("value");
//                        break;
//                    }
//                }
//            }
//
//            @Override
//            public boolean isReturn() {
//                return StringUtils.isNotEmpty(maxValue[0]);
//            }
//
//            @Override
//            public void processError(Exception e) {
//                LOG.warn(ExceptionUtil.getErrorMessage(e));
//            }
//        });
//
//        return maxValue[0];
//    }
//
//
//    /**
//     * 从数据库中查询增量字段的最大值
//     *
//     * @return
//     */
//    private String getMaxValueFromDb() {
//        String maxValue = null;
//        Connection conn = null;
//        Statement st = null;
//        ResultSet rs = null;
//        try {
//            long startTime = System.currentTimeMillis();
//
//            String queryMaxValueSql;
//            if (StringUtils.isNotEmpty(customSql)) {
//                queryMaxValueSql = String.format("select max(%s.%s) as max_value from ( %s ) %s", DbUtil.TEMPORARY_TABLE_NAME,
//                        databaseDialect.quoteColumn(incrementConfig.getColumnName()), customSql, DbUtil.TEMPORARY_TABLE_NAME);
//            } else {
//                queryMaxValueSql = String.format("select max(%s) as max_value from %s",
//                        databaseDialect.quoteColumn(incrementConfig.getColumnName()), databaseDialect.quoteTable(table));
//            }
//
//            String startSql = buildStartLocationSql(incrementConfig.getColumnType(),
//                    databaseDialect.quoteColumn(incrementConfig.getColumnName()),
//                    incrementConfig.getStartLocation(),
//                    incrementConfig.isUseMaxFunc());
//            if (StringUtils.isNotEmpty(startSql)) {
//                queryMaxValueSql += " where " + startSql;
//            }
//
//            LOG.info(String.format("Query max value sql is '%s'", queryMaxValueSql));
//
//            conn = getConnection();
//            st = conn.createStatement(resultSetType, resultSetConcurrency);
//            rs = st.executeQuery(queryMaxValueSql);
//            if (rs.next()) {
//                switch (type) {
//                    case TIMESTAMP:
//                        maxValue = String.valueOf(rs.getTimestamp("max_value").getTime());
//                        break;
//                    case DATE:
//                        maxValue = String.valueOf(rs.getDate("max_value").getTime());
//                        break;
//                    default:
//                        maxValue = StringUtil.stringToTimestampStr(String.valueOf(rs.getObject("max_value")), type);
//                }
//            }
//
//            LOG.info(String.format("Takes [%s] milliseconds to get the maximum value [%s]", System.currentTimeMillis() - startTime, maxValue));
//
//            return maxValue;
//        } catch (Throwable e) {
//            throw new RuntimeException("Get max value from " + table + " error", e);
//        } finally {
//            DbUtil.closeDbResources(rs, st, conn, false);
//        }
//    }
//
//    /**
//     * 执行查询
//     * @param startLocation
//     * @throws SQLException
//     */
//    @Override
//    protected void executeQuery(String startLocation) throws SQLException {
//        dbConn = getConnection();
//        // 部分驱动需要关闭事务自动提交，fetchSize参数才会起作用
//        dbConn.setAutoCommit(false);
//        if (incrementConfig.isPolling()) {
//            if(StringUtils.isBlank(startLocation)){
//                //从数据库中获取起始位置
//                queryStartLocation();
//            }else{
//                ps = dbConn.prepareStatement(querySql, resultSetType, resultSetConcurrency);
//                ps.setFetchSize(fetchSize);
//                ps.setQueryTimeout(queryTimeOut);
//                queryForPolling(startLocation);
//            }
//        }
//    }
//
//    /**
//     * 增量轮询查询
//     *
//     * @param startLocation
//     * @throws SQLException
//     */
//    @Override
//    protected void queryForPolling(String startLocation) throws SQLException {
//        LOG.trace("polling startLocation = {}", startLocation);
//        boolean isNumber = StringUtils.isNumeric(startLocation);
//        switch (type) {
//            case TIMESTAMP:
//                Timestamp ts = isNumber ? new Timestamp(Long.parseLong(startLocation)) : Timestamp.valueOf(startLocation);
//                ps.setTimestamp(1, ts);
//                break;
//            case DATE:
//                Date date = isNumber ? new Date(Long.parseLong(startLocation)) : Date.valueOf(startLocation);
//                ps.setDate(1, date);
//                break;
//            default:
//                if(isNumber){
//                    ps.setLong(1, Long.parseLong(startLocation));
//                }else {
//                    ps.setString(1, startLocation);
//                }
//        }
//        resultSet = ps.executeQuery();
//        hasNext = resultSet.next();
//    }
//
//    @Override
//    public boolean reachedEnd() throws IOException {
//        if (hasNext) {
//            return false;
//        } else {
//            if (incrementConfig.isPolling()) {
//                try {
//                    TimeUnit.MILLISECONDS.sleep(incrementConfig.getPollingInterval());
//                    //sqlserver、DB2驱动包不支持isValid()，这里先注释掉，后续更换驱动包
//                    //间隔轮询检测数据库连接是否断开，超时时间三秒，断开后自动重连
////                    if(!dbConn.isValid(3)){
////                        dbConn = DbUtil.getConnection(dbUrl, username, password);
////                        //重新连接后还是不可用则认为数据库异常，任务失败
////                        if(!dbConn.isValid(3)){
////                            String message = String.format("cannot connect to %s, username = %s, please check %s is available.", dbUrl, username, databaseInterface.getDatabaseType());
////                            LOG.error(message);
////                            throw new RuntimeException(message);
////                        }
////                    }
//                    if(!dbConn.getAutoCommit()){
//                        dbConn.setAutoCommit(true);
//                    }
//                    DbUtil.closeDbResources(resultSet, null, null, false);
//                    //此处endLocation理应不会为空
//                    queryForPolling(endLocationAccumulator.getLocalValue().toString());
//                    return false;
//                } catch (InterruptedException e) {
//                    LOG.warn("interrupted while waiting for polling, e = {}", ExceptionUtil.getErrorMessage(e));
//                } catch (SQLException e) {
//                    DbUtil.closeDbResources(resultSet, ps, null, false);
//                    String message = String.format("error to execute sql = %s, startLocation = %s, e = %s", querySql, endLocationAccumulator.getLocalValue(), ExceptionUtil.getErrorMessage(e));
//                    LOG.error(message);
//                    throw new RuntimeException(message, e);
//                }
//            }
//            return true;
//        }
//    }
//
//    /**
//     * 间隔轮询查询起始位置
//     * @throws SQLException
//     */
//    private void queryStartLocation() throws SQLException{
//        StringBuilder builder = new StringBuilder(128);
//        builder.append(querySql)
//                .append("ORDER BY ")
//                .append(JobCommonConstant.DOUBLE_QUOTE_MARK_SYMBOL)
//                .append(incrementConfig.getColumnName())
//                .append(JobCommonConstant.DOUBLE_QUOTE_MARK_SYMBOL);
//        ps = dbConn.prepareStatement(builder.toString(), resultSetType, resultSetConcurrency);
//        ps.setFetchSize(fetchSize);
//        //第一次查询数据库中增量字段的最大值
//        ps.setFetchDirection(ResultSet.FETCH_REVERSE);
//        ps.setQueryTimeout(queryTimeOut);
//        resultSet = ps.executeQuery();
//        hasNext = resultSet.next();
//
//        try {
//            //间隔轮询一直循环，直到查询到数据库中的数据为止
//            while(!hasNext){
//                TimeUnit.MILLISECONDS.sleep(incrementConfig.getPollingInterval());
//                //执行到此处代表轮询任务startLocation为空，且数据库中无数据，此时需要查询增量字段的最小值
//                ps.setFetchDirection(ResultSet.FETCH_FORWARD);
//                resultSet.close();
//                //如果事务不提交 就会导致数据库即使插入数据 也无法读到数据
//                dbConn.commit();
//                resultSet = ps.executeQuery();
//                hasNext = resultSet.next();
//                //每隔五分钟打印一次，(当前时间 - 任务开始时间) % 300秒 <= 一个间隔轮询周期
//                if((System.currentTimeMillis() - startTime) % 300000 <= incrementConfig.getPollingInterval()){
//                    LOG.info("no record matched condition in database, execute query sql = {}, startLocation = {}", querySql, endLocationAccumulator.getLocalValue());
//                }
//            }
//        } catch (InterruptedException e) {
//            LOG.warn("interrupted while waiting for polling, e = {}", ExceptionUtil.getErrorMessage(e));
//        }
//
//        //查询到数据，更新querySql
//        builder = new StringBuilder(128);
//        builder.append(querySql)
//                .append("and ")
//                .append(databaseDialect.quoteColumn(incrementConfig.getColumnName()))
//                .append(" > ? ORDER BY \"")
//                .append(incrementConfig.getColumnName())
//                .append(JobCommonConstant.DOUBLE_QUOTE_MARK_SYMBOL);
//        querySql = builder.toString();
//        ps = dbConn.prepareStatement(querySql, resultSetType, resultSetConcurrency);
//        ps.setFetchDirection(ResultSet.FETCH_REVERSE);
//        ps.setFetchSize(fetchSize);
//        ps.setQueryTimeout(queryTimeOut);
//        LOG.info("update querySql, sql = {}", querySql);
//    }
//
//}
