package com.leonside.dataroad.plugin.jdbc.reader;

import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.common.context.JobSetting;
import com.leonside.dataroad.common.domain.MetaColumn;
import com.leonside.dataroad.common.spi.ItemReader;
import com.leonside.dataroad.common.utils.ConfigBeanUtils;
import com.leonside.dataroad.common.utils.JsonUtil;
import com.leonside.dataroad.common.utils.ParameterUtils;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.flink.reader.BaseItemReader;
import com.leonside.dataroad.flink.utils.RawTypeUtils;
import com.leonside.dataroad.plugin.jdbc.DatabaseDialect;
import com.leonside.dataroad.plugin.jdbc.reader.config.JdbcReaderConfig;
import com.leonside.dataroad.plugin.jdbc.reader.config.JdbcReaderConfigKey;
import com.leonside.dataroad.plugin.jdbc.reader.inputformat.GenericJdbcInputFormat;
import com.leonside.dataroad.plugin.jdbc.reader.inputformat.GenericJdbcInputFormatBuilder;
import com.leonside.dataroad.plugin.jdbc.reader.inputformat.IncrementConfig;
import com.leonside.dataroad.plugin.jdbc.reader.support.QuerySqlBuilder;
import com.leonside.dataroad.plugin.jdbc.type.TypeConverterInterface;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.types.Row;

import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
@Data
public abstract class GenericJdbcReader extends BaseItemReader implements  ItemReader<FlinkExecuteContext, DataStream<Row>>, ComponentInitialization<FlinkExecuteContext,JdbcReaderConfig> {

    private JobSetting jobSetting;

    private JdbcReaderConfig jdbcReaderConfig;
//    protected String username;
//    protected String password;
//    protected String jdbcUrl;
//
//    protected String table;
//    protected String where;
//    protected String customSql;
//    protected String orderByColumn;
//
//    protected String splitKey;
//    protected int fetchSize;
//    protected int queryTimeOut;
//    protected List<MetaColumn> metaColumns;
//
//    protected IncrementConfig incrementConfig;


    protected DatabaseDialect databaseDialect;
    protected TypeConverterInterface typeConverter;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DataStream<Row> read(FlinkExecuteContext executeContext) throws Exception {

        if(jdbcReaderConfig.getFetchSize() == 0){
            jdbcReaderConfig.setFetchSize(databaseDialect.getFetchSize());
        }
        if(jdbcReaderConfig.getQueryTimeOut() == 0){
            jdbcReaderConfig.setQueryTimeOut(databaseDialect.getQueryTimeout());
        }

        GenericJdbcInputFormatBuilder builder = getGenericJdbcInputFormatBuilder();
        builder.setDriverName(databaseDialect.getDriverClass())
//                .setDbUrl(jdbcUrl)
//                .setUsername(username)
//                .setPassword(password)
//                .setTable(table)
//                .setMetaColumn(metaColumns)
//                .setFetchSize(fetchSize == 0 ? databaseDialect.getFetchSize() : fetchSize)
//                .setQueryTimeOut(queryTimeOut == 0 ? databaseDialect.getQueryTimeout() : queryTimeOut)
//                .setIncrementConfig(incrementConfig)
//                .setSplitKey(splitKey)
//                .setCustomSql(customSql)
                .jdbcReaderConfig(jdbcReaderConfig)
                .setDatabaseDialect(databaseDialect)
                .setBytes(bytes)
                .setMonitorUrls(monitorUrls)
                .setTypeConverter(typeConverter)
                .setNumPartitions(numPartitions)
                .setRestoreConfig(restoreConfig);

        QuerySqlBuilder sqlBuilder = new QuerySqlBuilder(this);
        builder.setQuery(sqlBuilder.buildSql());

        GenericJdbcInputFormat format =  builder.finish();

        executeContext.setStartMetaColumn(jdbcReaderConfig.getMetaColumns());

        TypeInformation rowTypeInfo = createRowTypeInfo(jdbcReaderConfig.getMetaColumns());

        return createInput(executeContext, format, rowTypeInfo);
    }

    private TypeInformation createRowTypeInfo(List<MetaColumn> metaColumns) {
        TypeInformation rowTypeInfo = null;
        if(CollectionUtils.isNotEmpty(metaColumns) && StringUtils.isNotEmpty(metaColumns.get(0).getType())){
            rowTypeInfo = RawTypeUtils.createRowTypeInfo(getDatabaseDialect().getRawTypeConverter(), metaColumns);
        }
        return rowTypeInfo;
    }


    protected abstract GenericJdbcInputFormatBuilder getGenericJdbcInputFormatBuilder() ;

//    @Override
//    public Class<? extends BaseConfig> configClass() {
//        return JdbcReaderConfig.class;
//    }

    @Override
    public void doInitialize(FlinkExecuteContext executeContext, JdbcReaderConfig config) {
        super.doInitialize(executeContext,config);
        this.jobSetting = executeContext.getJobSetting();
        this.restoreConfig = jobSetting.getRestore();
        jdbcReaderConfig = (JdbcReaderConfig)config;
//        jdbcUrl = ParameterUtils.getString(parameter, JdbcReaderConfigKey.KEY_JDBC_URL);
//        username = ParameterUtils.getString(parameter, JdbcReaderConfigKey.KEY_USER_NAME);
//        password = ParameterUtils.getString(parameter, JdbcReaderConfigKey.KEY_PASSWORD);
//        table = ParameterUtils.getString(parameter, JdbcReaderConfigKey.KEY_TABLE);
//        where = ParameterUtils.getString(parameter, JdbcReaderConfigKey.KEY_WHERE);
//        fetchSize = ParameterUtils.getInteger(parameter, JdbcReaderConfigKey.KEY_FETCH_SIZE);
//        queryTimeOut = ParameterUtils.getInteger(parameter, JdbcReaderConfigKey.KEY_QUERY_TIME_OUT);
//        splitKey = ParameterUtils.getString(parameter, JdbcReaderConfigKey.KEY_SPLIK_KEY);
//        customSql = ParameterUtils.getString(parameter, JdbcReaderConfigKey.KEY_CUSTOM_SQL);
//        orderByColumn = ParameterUtils.getString(parameter, JdbcReaderConfigKey.KEY_ORDER_BY_COLUMN);
        List<MetaColumn> metaColumns = MetaColumn.getMetaColumns(ParameterUtils.getArrayList(config.getParameter(), JdbcReaderConfigKey.KEY_COLUMN));
        jdbcReaderConfig.setMetaColumns(metaColumns);

        buildIncrementConfig(config.getParameter(), jdbcReaderConfig);

        this.databaseDialect = obtainDatabaseDialect();
    }

    protected abstract DatabaseDialect obtainDatabaseDialect();

    private void buildIncrementConfig(Map<String,Object> parameter,JdbcReaderConfig jdbcReaderConfig){
//        boolean polling = ParameterUtils.getBooleanNullable(parameter, JdbcReaderConfigKey.KEY_POLLING.getName(), false);
//        String startLocation = ParameterUtils.getStringNullable(parameter, JdbcReaderConfigKey.KEY_START_LOCATION.getName());
//        boolean useMaxFunc = ParameterUtils.getBooleanNullable(parameter, JdbcReaderConfigKey.KEY_USE_MAX_FUNC.getName(), false);
//        int requestAccumulatorInterval = ParameterUtils.getIntegerNullable(parameter, JdbcReaderConfigKey.KEY_REQUEST_ACCUMULATOR_INTERVAL.getName(), 2);
//        long pollingInterval = ParameterUtils.getIntegerNullable(parameter, JdbcReaderConfigKey.KEY_POLLING_INTERVAL.getName(), 5000);
        String increColumn = ParameterUtils.getStringNullable(parameter, JdbcReaderConfigKey.KEY_INCRE_COLUMN.getName());

        IncrementConfig incrementConfig = new IncrementConfig();
        jdbcReaderConfig.setIncrementConfig(incrementConfig);
        ConfigBeanUtils.copyConfig(incrementConfig, parameter, JdbcReaderConfigKey.class);
        //增量字段不为空，表示任务为增量或间隔轮询任务
        if (StringUtils.isNotBlank(increColumn)){
            String type = null;
            String name = null;
            int index = -1;

            for (MetaColumn metaColumn : jdbcReaderConfig.getMetaColumns()) {
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
                                JsonUtil.getInstance().writeJson(jdbcReaderConfig.getMetaColumns()),
                                increColumn));
            }

            incrementConfig.setIncrement(true);
            incrementConfig.setColumnName(name);
            incrementConfig.setColumnType(type);
            incrementConfig.setColumnIndex(index);
//            incrementConfig.setPolling(polling);
//            incrementConfig.setStartLocation(startLocation);
//            incrementConfig.setUseMaxFunc(useMaxFunc);
//            incrementConfig.setRequestAccumulatorInterval(requestAccumulatorInterval);
//            incrementConfig.setPollingInterval(pollingInterval);
        }
    }

}
