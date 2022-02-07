package com.leonside.dataroad.plugin.jdbc.lookup.function;

import com.google.common.collect.Lists;
import com.leonside.dataroad.common.exception.JobException;
import com.leonside.dataroad.flink.processor.lookup.function.AbstractLruLookupFunction;
import com.leonside.dataroad.flink.utils.RowUtils;
import com.leonside.dataroad.plugin.jdbc.DatabaseDialect;
import com.leonside.dataroad.plugin.jdbc.lookup.config.JdbcLookupConfig;
import com.leonside.dataroad.plugin.jdbc.lookup.datasource.DruidDataSourceProvider;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * @author leon
 */
public class JdbcLruLookupFunction extends AbstractLruLookupFunction {
    public static final String DRUID_PREFIX = "druid.";
    public static final Logger LOG = LoggerFactory.getLogger(JdbcLruLookupFunction.class);
    private transient ThreadPoolExecutor executor;

    /** when network is unhealthy block query */
    private final AtomicBoolean connectionStatus = new AtomicBoolean(true);
    /** vertx */
    private transient Vertx vertx;
    /** rdb client */
    private transient SQLClient rdbSqlClient;

    private JdbcLookupConfig jdbcLookupConfig;
    private DatabaseDialect databaseDialect;

    private final String query;
    /** vertx async pool size */
    protected int asyncPoolSize;

    public JdbcLruLookupFunction(JdbcLookupConfig jdbcLookupConfig, DatabaseDialect databaseDialect) {
       super(jdbcLookupConfig);
        this.jdbcLookupConfig = jdbcLookupConfig;
        this.query = (StringUtils.isNotEmpty(jdbcLookupConfig.getCustomSql())) ?
                databaseDialect.getSelectFromStatement(jdbcLookupConfig.getCustomSql(), valueColumns) :
                databaseDialect.getSelectFromStatement(jdbcLookupConfig.getSchema(), jdbcLookupConfig.getTable(), baseLookupConfig.getColumns(), jdbcLookupConfig.getWhere(), valueColumns);
        this.databaseDialect = databaseDialect;
        this.asyncPoolSize = jdbcLookupConfig.getAsyncPoolSize();
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        asyncPoolSize = asyncPoolSize > 0 ? asyncPoolSize : jdbcLookupConfig.getDbConnPoolSize();

        VertxOptions vertxOptions = new VertxOptions();
        JsonObject jdbcConfig = createJdbcConfig(jdbcLookupConfig.getDruidConf());
        System.setProperty("vertx.disableFileCPResolving", "true");
        vertxOptions
                .setEventLoopPoolSize(jdbcLookupConfig.getEventLoopPoolSize())
                .setWorkerPoolSize(asyncPoolSize)
                .setFileResolverCachingEnabled(false);

        this.vertx = Vertx.vertx(vertxOptions);
        this.rdbSqlClient = JDBCClient.createNonShared(vertx, jdbcConfig);

        executor = new ThreadPoolExecutor(
                        jdbcLookupConfig.getDbConnPoolSize(),
                        jdbcLookupConfig.getDbConnPoolSize(),
                        0,
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingQueue<>(jdbcLookupConfig.getTaskQueueSize()),
                        new ThreadFactory(){
                                AtomicInteger i = new AtomicInteger(0);
                                @Override
                                public Thread newThread(Runnable r) {
                                    Thread t = new Thread(r);
                                    t.setName("vertx-thread-pool-" + i.incrementAndGet());
                                    return t;
                                }} ,
                        new ThreadPoolExecutor.CallerRunsPolicy());
        LOG.info("async dim table JdbcOptions info: {} ", jdbcLookupConfig.toString());

    }

    @Override
    protected void doAsyncInvoke(Row row, ResultFuture<Row> resultFuture) throws InterruptedException {
        AtomicLong networkLogCounter = new AtomicLong(0L);
        // network is unhealthy
        while (!connectionStatus.get()) {
            if (networkLogCounter.getAndIncrement() % 1000 == 0) {
                LOG.info("network unhealthy to block task");
            }
            Thread.sleep(100);
        }

        executor.execute(() ->connectWithRetry(row, resultFuture,rdbSqlClient));
    }

    /**
     * @param resultFuture
     * @param rdbSqlClient 数据库客户端
     */
    private void connectWithRetry(Row row,ResultFuture<Row> resultFuture, SQLClient rdbSqlClient)  {
        AtomicLong failCounter = new AtomicLong(0);
        AtomicBoolean finishFlag = new AtomicBoolean(false);
        while (!finishFlag.get()) {
            try {
                CountDownLatch latch = new CountDownLatch(1);
                asyncQueryData(row, resultFuture, rdbSqlClient, failCounter, finishFlag, latch);
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    LOG.error("", e);
                }
            } catch (Exception e) {
                // 数据源队列溢出情况
                connectionStatus.set(false);
            }
            if (!finishFlag.get()) {
                try {
                    Thread.sleep(10L);
                } catch (InterruptedException e) {
                    logger.error("",e);
                }
            }
        }
    }

    /**
     * 执行异步查询
     *
     * @param resultFuture
     * @param rdbSqlClient 数据库客户端
     * @param failCounter 失败次数
     * @param finishFlag 完成标识
     * @param latch 同步标识
     */
    protected final void asyncQueryData(
            Row row,
            ResultFuture<Row> resultFuture,
            SQLClient rdbSqlClient,
            AtomicLong failCounter,
            AtomicBoolean finishFlag,
            CountDownLatch latch) {
        rdbSqlClient.getConnection(
                conn -> {
                    try {
                        Integer retryMaxNum = jdbcLookupConfig.getMaxRetryTimes();
                        int logPrintTime = retryMaxNum / jdbcLookupConfig.getErrorLogPrintNum() == 0
                                        ? retryMaxNum
                                        : retryMaxNum / jdbcLookupConfig.getErrorLogPrintNum();
                        if (conn.failed()) {
                            connectionStatus.set(false);
                            if (failCounter.getAndIncrement() % logPrintTime == 0) {
                                LOG.error("getConnection error. ", conn.cause());
                            }
                            LOG.error( String.format("retry ... current time [%s]", failCounter.get()));
                            if (failCounter.get() >= retryMaxNum) {
                                resultFuture.completeExceptionally(new JobException("The maximum number of retries exceeded ",conn.cause()));
                                finishFlag.set(true);
                            }
                            return;
                        }
                        connectionStatus.set(true);

                        handleQuery(row , conn.result(), resultFuture);
                        finishFlag.set(true);
                    } catch (Exception e) {
                        dealFillDataError(resultFuture, e);
                    } finally {
                        latch.countDown();
                    }
                });
    }



    /**
     * 执行异步查询
     *
     * @param connection 连接
     * @param resultFuture
     */
    private void handleQuery(Row row,
            SQLConnection connection,
            ResultFuture<Row> resultFuture) {
        JsonArray params = new JsonArray();
        Stream.of(keyColumns).forEach(key->{
            params.add(row.getField(key));
        });
        connection.queryWithParams(
                query,
                params,
                rs -> {
                    try {
                        if (rs.failed()) {
                            LOG.error(String.format("\nget data with sql [%s],data [%s] failed! \ncause: [%s]",query, params, rs.cause().getMessage()));
                            throw new RuntimeException(rs.cause().getMessage(), rs.cause());
                        }

                        int resultSize = rs.result().getResults().size();
                        if (resultSize > 0) {

                            for (JsonArray line : rs.result().getResults()) {
                                try {
                                    String[] columns = jdbcLookupConfig.getColumns();
                                    Row lookupRow = Row.withNames();
                                    for (int i = 0; i < columns.length; i++) {
                                        lookupRow.setField(columns[i], line.getValue(i));
                                    }
                                    putCache(buildCacheKey(row, keyColumns), lookupRow);
                                    resultFuture.complete(Lists.newArrayList(RowUtils.combineRowWithNames(row, lookupRow, valueColumns)));
                                } catch (Exception e) {
                                    // todo 这里需要抽样打印
                                    LOG.error("error:{} \n sql:{} \n data:{}", e.getMessage(), query, line);
                                }
                            }
                        } else {
                            dealMissKey(resultFuture);
                        }
                    } finally {
                        // and close the connection
                        connection.close(
                                done -> {
                                    if (done.failed()) {
                                        LOG.error("sql connection close failed! ", done.cause());
                                    }
                                });
                    }
                });
    }

    @Override
    public void close() throws Exception {
        super.close();
        if (rdbSqlClient != null) {
            rdbSqlClient.close();
        }

        if (executor != null) {
            executor.shutdown();
        }
        // 关闭异步连接vertx事件循环线程，因为vertx使用的是非守护线程
        if (Objects.nonNull(vertx)) {
            vertx.close(
                    done -> {
                        if (done.failed()) {
                            LOG.error("vert.x close error. cause by {}", done.cause().getMessage());
                        }
                    });
        }
    }

    public JsonObject createJdbcConfig(Map<String, Object> druidConfMap) {
        JsonObject clientConfig = new JsonObject(druidConfMap);
        clientConfig
                .put(DRUID_PREFIX + "url", jdbcLookupConfig.getJdbcUrl())
                .put(DRUID_PREFIX + "username", jdbcLookupConfig.getUsername())
                .put(DRUID_PREFIX + "password", jdbcLookupConfig.getPassword())
                .put(DRUID_PREFIX + "driverClassName", databaseDialect.getDriverClass())
                .put("provider_class", DruidDataSourceProvider.class.getName())
                .put(DRUID_PREFIX + "maxActive", asyncPoolSize);

        return clientConfig;
    }

    public static class JdbcLruLookupFunctionBuilder{

        private JdbcLookupConfig jdbcLookupConfig;
        private DatabaseDialect databaseDialect;

        public JdbcLruLookupFunction build(){
            return new JdbcLruLookupFunction(jdbcLookupConfig, databaseDialect);
        }

        public JdbcLruLookupFunctionBuilder jdbcLookupConfig(JdbcLookupConfig lookupConfig) {
            this.jdbcLookupConfig = lookupConfig;
            return this;
        }

        public JdbcLruLookupFunctionBuilder databaseDialect(DatabaseDialect databaseDialect) {
            this.databaseDialect = databaseDialect;
            return this;
        }
    }

}
