package com.leonside.dataroad.plugin.rdb;

import com.leonside.dataroad.common.context.JobSetting;
import com.leonside.dataroad.common.domain.MetaColumn;
import com.leonside.dataroad.common.spi.ItemReader;
import com.leonside.dataroad.common.utils.JsonUtil;
import com.leonside.dataroad.common.utils.MapParameterUtils;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.plugin.rdb.reader.IncrementConfig;
import com.leonside.dataroad.plugin.rdb.reader.JdbcConfigKeys;
import com.leonside.dataroad.plugin.rdb.reader.QuerySqlBuilder;
import com.leonside.dataroad.plugin.rdb.type.TypeConverterInterface;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.flink.api.common.io.InputFormat;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author leon
 */
@Data
public abstract class GenericJdbcReader extends BaseDataReader implements  ItemReader<FlinkExecuteContext, DataStream<Row>> {

    //baseReader

    //jdbcReader
    private JobSetting jobSetting;

    protected String username;
    protected String password;
    protected String dbUrl;
    protected Properties properties;  //todo

    protected String table;
    protected String where;
    protected String customSql;
    protected String orderByColumn;

    protected String splitKey;
    protected int fetchSize;
    protected int queryTimeOut;

    protected IncrementConfig incrementConfig;
    protected DatabaseDialect databaseDialect;
    protected TypeConverterInterface typeConverter;
    protected List<MetaColumn> metaColumns;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DataStream<Row> read(FlinkExecuteContext executeContext) throws Exception {

        GenericJdbcInputFormatBuilder builder = getGenericJdbcInputFormatBuilder();
        builder.setDriverName(databaseDialect.getDriverClass());
        builder.setDbUrl(dbUrl);
        builder.setUsername(username);
        builder.setPassword(password);
        builder.setBytes(bytes);
        builder.setMonitorUrls(monitorUrls);
        builder.setTable(table);
        builder.setDatabaseDialect(databaseDialect);
        builder.setTypeConverter(typeConverter);
        builder.setMetaColumn(metaColumns);
        builder.setFetchSize(fetchSize == 0 ? databaseDialect.getFetchSize() : fetchSize);
        builder.setQueryTimeOut(queryTimeOut == 0 ? databaseDialect.getQueryTimeout() : queryTimeOut);
        builder.setIncrementConfig(incrementConfig);
        builder.setSplitKey(splitKey);
        builder.setNumPartitions(numPartitions);
        builder.setCustomSql(customSql);
        builder.setProperties(properties);
        builder.setRestoreConfig(restoreConfig);
//        builder.setHadoopConfig(hadoopConfig);
//        builder.setTestConfig(testConfig);
//        builder.setLogConfig(logConfig);

        QuerySqlBuilder sqlBuilder = new QuerySqlBuilder(this);
        builder.setQuery(sqlBuilder.buildSql());

        GenericJdbcInputFormat format =  builder.finish();
        return createInput(executeContext, format);
    }

    protected DataStream<Row> createInput(FlinkExecuteContext executeContext,InputFormat inputFormat) {
        return createInput(executeContext,inputFormat,this.getClass().getSimpleName().toLowerCase());
    }


    private DataStream<Row> createInput(FlinkExecuteContext executeContext,InputFormat inputFormat, String sourceName) {
        Preconditions.checkNotNull(sourceName);
        Preconditions.checkNotNull(inputFormat);
        TypeInformation typeInfo = TypeExtractor.getInputFormatTypes(inputFormat);
        GenericInputFormatSourceFunction function = new GenericInputFormatSourceFunction(inputFormat, typeInfo);
        return env.addSource(function, sourceName, typeInfo);

//        DtInputFormatSourceFunction function = new DtInputFormatSourceFunction(inputFormat, typeInfo);
//        return executeContext.getEnvironment().createInput(inputFormat, typeInfo, sourceName);
//        return executeContext.getEnvironment().addSource(new GenericInputFormatSourceFunction<>(inputFormat, typeInfo), sourceName);
    }

    protected abstract GenericJdbcInputFormatBuilder getGenericJdbcInputFormatBuilder() ;

    @Override
    public void initialize(FlinkExecuteContext executeContext, Map<String, Object> parameter) {
        super.initialize(executeContext,parameter);
        this.jobSetting = executeContext.getJobSetting();
        this.restoreConfig = jobSetting.getRestore();
//        "parameter": {
//            "table": ["schema.table"],
//            "password": "passwd",
//                    "database": "schema",
//                    "port": 3306,
//                    "cat": "insert,update,delete",
//                    "host": "host",
//                    "jdbcUrl": "jdbc:mysql://host:3306/schema",
//                    "pavingData": true,
//                    "username": "user"
//        },"column": [
//            {
//              "name": "id",
//              "type": "int"
//            },
//            {
//              "name": "user_id",
//              "type": "int"
//            },

        dbUrl =MapParameterUtils.getString(parameter,JdbcConfigKeys.KEY_JDBC_URL);
        username = MapParameterUtils.getString(parameter,JdbcConfigKeys.KEY_USER_NAME);
        password = MapParameterUtils.getString(parameter,JdbcConfigKeys.KEY_PASSWORD);
        table = MapParameterUtils.getStringNullable(parameter,JdbcConfigKeys.KEY_TABLE);
        where = MapParameterUtils.getStringNullable(parameter,JdbcConfigKeys.KEY_WHERE);
        metaColumns = MetaColumn.getMetaColumns(MapParameterUtils.getArrayListNullable(parameter, JdbcConfigKeys.KEY_COLUMN));
        fetchSize = MapParameterUtils.getIntegerNullable(parameter,JdbcConfigKeys.KEY_FETCH_SIZE,0);
        queryTimeOut = MapParameterUtils.getIntegerNullable(parameter,JdbcConfigKeys.KEY_QUERY_TIME_OUT,0);
        splitKey = MapParameterUtils.getStringNullable(parameter,JdbcConfigKeys.KEY_SPLIK_KEY);
        customSql = MapParameterUtils.getStringNullable(parameter,JdbcConfigKeys.KEY_CUSTOM_SQL);
        orderByColumn = MapParameterUtils.getStringNullable(parameter,JdbcConfigKeys.KEY_ORDER_BY_COLUMN);
        //todo
//        properties = readerConfig.getParameter().getProperties(JdbcConfigKeys.KEY_PROPERTIES, null);

        buildIncrementConfig(parameter);

        this.databaseDialect = obtainDatabaseDialect();
    }

    protected abstract DatabaseDialect obtainDatabaseDialect();

    private void buildIncrementConfig(Map<String,Object> parameter){
        boolean polling = MapParameterUtils.getBooleanNullable(parameter,JdbcConfigKeys.KEY_POLLING, false);
        String increColumn = MapParameterUtils.getStringNullable(parameter,JdbcConfigKeys.KEY_INCRE_COLUMN);
        String startLocation = MapParameterUtils.getStringNullable(parameter,JdbcConfigKeys.KEY_START_LOCATION);
        boolean useMaxFunc = MapParameterUtils.getBooleanNullable(parameter,JdbcConfigKeys.KEY_USE_MAX_FUNC, false);
        int requestAccumulatorInterval = MapParameterUtils.getIntegerNullable(parameter,JdbcConfigKeys.KEY_REQUEST_ACCUMULATOR_INTERVAL, 2);
        long pollingInterval = MapParameterUtils.getIntegerNullable(parameter,JdbcConfigKeys.KEY_POLLING_INTERVAL, 5000);

        incrementConfig = new IncrementConfig();
        //增量字段不为空，表示任务为增量或间隔轮询任务
        if (StringUtils.isNotBlank(increColumn)){
            String type = null;
            String name = null;
            int index = -1;

            //纯数字则表示增量字段在column中的顺序位置
            if(NumberUtils.isNumber(increColumn)){
                int idx = Integer.parseInt(increColumn);
                if(idx > metaColumns.size() - 1){
                    throw new RuntimeException(
                            String.format("config error : incrementColumn must less than column.size() when increColumn is number, column = %s, size = %s, increColumn = %s",
                                    JsonUtil.getInstance().writeJson(metaColumns),
                                    metaColumns.size(),
                                    increColumn));
                }
                MetaColumn metaColumn = metaColumns.get(idx);
                type = metaColumn.getType();
                name = metaColumn.getName();
                index = metaColumn.getIndex();
            } else {
                for (MetaColumn metaColumn : metaColumns) {
                    if(Objects.equals(increColumn, metaColumn.getName())){
                        type = metaColumn.getType();
                        name = metaColumn.getName();
                        index = metaColumn.getIndex();
                        break;
                    }
                }
            }
            if (type == null || name == null){
                throw new IllegalArgumentException(
                        String.format("config error : increColumn's name or type is null, column = %s, increColumn = %s",
                                JsonUtil.getInstance().writeJson(metaColumns),
                                increColumn));
            }

            incrementConfig.setIncrement(true);
            incrementConfig.setPolling(polling);
            incrementConfig.setColumnName(name);
            incrementConfig.setColumnType(type);
            incrementConfig.setStartLocation(startLocation);
            incrementConfig.setUseMaxFunc(useMaxFunc);
            incrementConfig.setColumnIndex(index);
            incrementConfig.setRequestAccumulatorInterval(requestAccumulatorInterval);
            incrementConfig.setPollingInterval(pollingInterval);
        }
    }

}
