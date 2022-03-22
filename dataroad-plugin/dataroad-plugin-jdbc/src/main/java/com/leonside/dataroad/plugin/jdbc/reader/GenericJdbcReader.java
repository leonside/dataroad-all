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

        if (executeContext.getEnvironment().getParallelism() > 1){
            throw new IllegalArgumentException("Must specify the split column when the Parallelism is greater than 1");
        }

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

    @Override
    public void doInitialize(FlinkExecuteContext executeContext, JdbcReaderConfig config) {
        super.doInitialize(executeContext,config);
        this.jobSetting = executeContext.getJobSetting();
        this.restoreConfig = jobSetting.getRestore();
        jdbcReaderConfig = config;

        List<MetaColumn> metaColumns = MetaColumn.getMetaColumns(ParameterUtils.getArrayList(config.getParameter(), JdbcReaderConfigKey.KEY_COLUMN));
        jdbcReaderConfig.setMetaColumns(metaColumns);

        buildIncrementConfig(config.getParameter(), jdbcReaderConfig);

        this.databaseDialect = obtainDatabaseDialect();
    }

    protected abstract DatabaseDialect obtainDatabaseDialect();

    private void buildIncrementConfig(Map<String,Object> parameter,JdbcReaderConfig jdbcReaderConfig){
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

        }
    }

}
