package com.leonside.dataroad.plugin.jdbc.reader;

import com.leonside.dataroad.common.context.JobSetting;
import com.leonside.dataroad.common.domain.MetaColumn;
import com.leonside.dataroad.common.spi.ItemReader;
import com.leonside.dataroad.common.utils.JsonUtil;
import com.leonside.dataroad.common.utils.ParameterUtils;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.flink.reader.BaseItemReader;
import com.leonside.dataroad.plugin.jdbc.DatabaseDialect;
import com.leonside.dataroad.plugin.jdbc.reader.inputformat.GenericJdbcInputFormat;
import com.leonside.dataroad.plugin.jdbc.reader.inputformat.GenericJdbcInputFormatBuilder;
import com.leonside.dataroad.plugin.jdbc.reader.inputformat.IncrementConfig;
import com.leonside.dataroad.plugin.jdbc.reader.support.QuerySqlBuilder;
import com.leonside.dataroad.plugin.jdbc.type.TypeConverterInterface;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

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

        executeContext.setStartMetaColumn(metaColumns);

        return createInput(executeContext, format);
    }



    protected abstract GenericJdbcInputFormatBuilder getGenericJdbcInputFormatBuilder() ;

    @Override
    public void initialize(FlinkExecuteContext executeContext, Map<String, Object> parameter) {
        super.initialize(executeContext,parameter);
        this.jobSetting = executeContext.getJobSetting();
        this.restoreConfig = jobSetting.getRestore();
        dbUrl = ParameterUtils.getString(parameter, JdbcReaderKey.KEY_JDBC_URL);
        username = ParameterUtils.getString(parameter, JdbcReaderKey.KEY_USER_NAME);
        password = ParameterUtils.getString(parameter, JdbcReaderKey.KEY_PASSWORD);
        table = ParameterUtils.getString(parameter, JdbcReaderKey.KEY_TABLE);
        where = ParameterUtils.getString(parameter, JdbcReaderKey.KEY_WHERE);
        metaColumns = MetaColumn.getMetaColumns(ParameterUtils.getArrayList(parameter, JdbcReaderKey.KEY_COLUMN));
        fetchSize = ParameterUtils.getInteger(parameter, JdbcReaderKey.KEY_FETCH_SIZE);
        queryTimeOut = ParameterUtils.getInteger(parameter, JdbcReaderKey.KEY_QUERY_TIME_OUT);
        splitKey = ParameterUtils.getString(parameter, JdbcReaderKey.KEY_SPLIK_KEY);
        customSql = ParameterUtils.getString(parameter, JdbcReaderKey.KEY_CUSTOM_SQL);
        orderByColumn = ParameterUtils.getString(parameter, JdbcReaderKey.KEY_ORDER_BY_COLUMN);

        buildIncrementConfig(parameter);

        this.databaseDialect = obtainDatabaseDialect();
    }

    protected abstract DatabaseDialect obtainDatabaseDialect();

    private void buildIncrementConfig(Map<String,Object> parameter){
        boolean polling = ParameterUtils.getBooleanNullable(parameter, JdbcReaderKey.KEY_POLLING.getName(), false);
        String increColumn = ParameterUtils.getStringNullable(parameter, JdbcReaderKey.KEY_INCRE_COLUMN.getName());
        String startLocation = ParameterUtils.getStringNullable(parameter, JdbcReaderKey.KEY_START_LOCATION.getName());
        boolean useMaxFunc = ParameterUtils.getBooleanNullable(parameter, JdbcReaderKey.KEY_USE_MAX_FUNC.getName(), false);
        int requestAccumulatorInterval = ParameterUtils.getIntegerNullable(parameter, JdbcReaderKey.KEY_REQUEST_ACCUMULATOR_INTERVAL.getName(), 2);
        long pollingInterval = ParameterUtils.getIntegerNullable(parameter, JdbcReaderKey.KEY_POLLING_INTERVAL.getName(), 5000);

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
