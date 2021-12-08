package com.leonside.dataroad.plugin.rdb.reader;

import com.leonside.dataroad.common.context.JobSetting;
import com.leonside.dataroad.common.domain.MetaColumn;
import com.leonside.dataroad.common.spi.ItemReader;
import com.leonside.dataroad.common.utils.JsonUtil;
import com.leonside.dataroad.common.utils.MapParameterUtils;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.flink.reader.BaseItemReader;
import com.leonside.dataroad.plugin.rdb.DatabaseDialect;
import com.leonside.dataroad.plugin.rdb.constant.JdbcReaderKey;
import com.leonside.dataroad.plugin.rdb.inputformat.GenericInputFormatSourceFunction;
import com.leonside.dataroad.plugin.rdb.inputformat.GenericJdbcInputFormat;
import com.leonside.dataroad.plugin.rdb.inputformat.GenericJdbcInputFormatBuilder;
import com.leonside.dataroad.plugin.rdb.inputformat.IncrementConfig;
import com.leonside.dataroad.plugin.rdb.support.QuerySqlBuilder;
import com.leonside.dataroad.plugin.rdb.type.TypeConverterInterface;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.io.InputFormat;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;
import org.apache.flink.util.Preconditions;

import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
@Data
public abstract class GenericJdbcReader extends BaseItemReader implements  ItemReader<FlinkExecuteContext, DataStream<Row>> {

    private JobSetting jobSetting;

    protected String username;
    protected String password;
    protected String dbUrl;

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
        builder.setDriverName(databaseDialect.getDriverClass())
                .setDbUrl(dbUrl)
                .setUsername(username)
                .setPassword(password)
                .setBytes(bytes)
                .setMonitorUrls(monitorUrls)
                .setTable(table)
                .setDatabaseDialect(databaseDialect)
                .setTypeConverter(typeConverter)
                .setMetaColumn(metaColumns)
                .setFetchSize(fetchSize == 0 ? databaseDialect.getFetchSize() : fetchSize)
                .setQueryTimeOut(queryTimeOut == 0 ? databaseDialect.getQueryTimeout() : queryTimeOut)
                .setIncrementConfig(incrementConfig)
                .setSplitKey(splitKey)
                .setNumPartitions(numPartitions)
                .setCustomSql(customSql)
                .setRestoreConfig(restoreConfig);

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
    }

    protected abstract GenericJdbcInputFormatBuilder getGenericJdbcInputFormatBuilder() ;

    @Override
    public void initialize(FlinkExecuteContext executeContext, Map<String, Object> parameter) {
        super.initialize(executeContext,parameter);
        this.jobSetting = executeContext.getJobSetting();
        this.restoreConfig = jobSetting.getRestore();
        dbUrl =MapParameterUtils.getString(parameter, JdbcReaderKey.KEY_JDBC_URL.getName());
        username = MapParameterUtils.getString(parameter, JdbcReaderKey.KEY_USER_NAME.getName());
        password = MapParameterUtils.getString(parameter, JdbcReaderKey.KEY_PASSWORD.getName());
        table = MapParameterUtils.getStringNullable(parameter, JdbcReaderKey.KEY_TABLE.getName());
        where = MapParameterUtils.getStringNullable(parameter, JdbcReaderKey.KEY_WHERE.getName());
        metaColumns = MetaColumn.getMetaColumns(MapParameterUtils.getArrayListNullable(parameter, JdbcReaderKey.KEY_COLUMN.getName()));
        fetchSize = MapParameterUtils.getIntegerNullable(parameter, JdbcReaderKey.KEY_FETCH_SIZE.getName(),0);
        queryTimeOut = MapParameterUtils.getIntegerNullable(parameter, JdbcReaderKey.KEY_QUERY_TIME_OUT.getName(),0);
        splitKey = MapParameterUtils.getStringNullable(parameter, JdbcReaderKey.KEY_SPLIK_KEY.getName());
        customSql = MapParameterUtils.getStringNullable(parameter, JdbcReaderKey.KEY_CUSTOM_SQL.getName());
        orderByColumn = MapParameterUtils.getStringNullable(parameter, JdbcReaderKey.KEY_ORDER_BY_COLUMN.getName());

        buildIncrementConfig(parameter);

        this.databaseDialect = obtainDatabaseDialect();
    }

    protected abstract DatabaseDialect obtainDatabaseDialect();

    private void buildIncrementConfig(Map<String,Object> parameter){
        boolean polling = MapParameterUtils.getBooleanNullable(parameter, JdbcReaderKey.KEY_POLLING.getName(), false);
        String increColumn = MapParameterUtils.getStringNullable(parameter, JdbcReaderKey.KEY_INCRE_COLUMN.getName());
        String startLocation = MapParameterUtils.getStringNullable(parameter, JdbcReaderKey.KEY_START_LOCATION.getName());
        boolean useMaxFunc = MapParameterUtils.getBooleanNullable(parameter, JdbcReaderKey.KEY_USE_MAX_FUNC.getName(), false);
        int requestAccumulatorInterval = MapParameterUtils.getIntegerNullable(parameter, JdbcReaderKey.KEY_REQUEST_ACCUMULATOR_INTERVAL.getName(), 2);
        long pollingInterval = MapParameterUtils.getIntegerNullable(parameter, JdbcReaderKey.KEY_POLLING_INTERVAL.getName(), 5000);

        incrementConfig = new IncrementConfig();
        //增量字段不为空，表示任务为增量或间隔轮询任务
        if (StringUtils.isNotBlank(increColumn)){
            String type = null;
            String name = null;
            int index = -1;

            for (MetaColumn metaColumn : metaColumns) {
                if(increColumn.equalsIgnoreCase(metaColumn.getName())){
                    type = metaColumn.getType();
                    name = metaColumn.getName();
                    index = metaColumn.getIndex();
                    break;
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
