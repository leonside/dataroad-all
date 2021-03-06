package com.leonside.dataroad.flink.writer;

import com.leonside.dataroad.common.config.BaseConfig;
import com.leonside.dataroad.common.context.RestoreConfig;
import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameSupport;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import com.leonside.dataroad.flink.writer.outputformat.GenericOutputFormatSinkFunction;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSink;
import org.apache.flink.util.Preconditions;

import java.util.UUID;

/**
 * Abstract specification of Writer Plugin
 *
 */
public abstract class BaseItemWriter extends ComponentNameSupport  {

    protected String mode;

    protected String monitorUrls;

    protected Integer errors;

    protected Double errorRatio;

    protected RestoreConfig restoreConfig;

    public void doInitialize(FlinkExecuteContext executeContext, BaseConfig baseConfig) {
        this.restoreConfig = executeContext.getJobSetting().getRestore();

        if (restoreConfig.isStream()) {
            return;
        }
// todo
//        if (restoreConfig.isRestore()) {
//            MetaColumn metaColumn = MetaColumn.getMetaColumn(columns, restoreConfig.getRestoreColumnName());
//            if (metaColumn == null) {
//                throw new RuntimeException("Can not find restore column from json with column name:" + restoreConfig.getRestoreColumnName());
//            }
//            restoreConfig.setRestoreColumnIndex(metaColumn.getIndex());
//            restoreConfig.setRestoreColumnType(metaColumn.getType());
//        }
    }

    @SuppressWarnings("unchecked")
    protected DataStreamSink<?> createOutput(DataStream<?> dataSet, OutputFormat outputFormat, String sinkName) {
        Preconditions.checkNotNull(dataSet);
        Preconditions.checkNotNull(sinkName);
        Preconditions.checkNotNull(outputFormat);

        DataStreamSink<?> dataStreamSink = dataSet.addSink(new GenericOutputFormatSinkFunction<>(outputFormat));
        dataStreamSink.name(sinkName);

        return dataStreamSink;
    }

    protected DataStreamSink<?> createOutput(DataStream<?> dataSet, OutputFormat outputFormat) {
        return createOutput(dataSet, outputFormat, this.getClass().getSimpleName().toLowerCase() );
    }

}
