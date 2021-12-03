package com.leonside.dataroad.flink;

import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.context.JobSetting;
import com.leonside.dataroad.common.spi.JobExecutionListener;
import com.leonside.dataroad.core.Job;
import com.leonside.dataroad.core.spi.JobEngineProvider;
import com.leonside.dataroad.core.flow.SimpleJobFlow;
import com.leonside.dataroad.core.support.LoggerHelper;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

/**
 * @author leon
 */
public class FlinkJobProvider implements JobEngineProvider<FlinkExecuteContext> {
    @Override
    public Job createJob(FlinkExecuteContext executeContext, SimpleJobFlow startJobFlow) {

        //checkpoint\configuration\Parallelism\isolation classloader

        buildGlobalSetting(executeContext, startJobFlow);

        initLoggerSetting(executeContext);
        //metric??
//
//        buildJobExecuteListener();

        return new FlinkJob(executeContext, startJobFlow);
    }

    @Override
    public FlinkExecuteContext createExecuteContext(JobSetting jobSetting, List<JobExecutionListener> executionListeners) {

        ExecuteContext executeContext = ExecuteContext.of(jobSetting, executionListeners);

        StreamExecutionEnvironment environment = buildStreamExecutionEnvironment(executeContext);

        if(jobSetting.getRestore().isRestore()){
            //// TODO: 2021/12/3
            if(jobSetting.getRestore().getSavepointInterval() != null){
                environment.enableCheckpointing(jobSetting.getRestore().getSavepointInterval());
            }
            environment.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
            environment.setStateBackend(new FsStateBackend(jobSetting.getRestore().getSavepointPath()));
        }


        return buildFlinkExecuteContext(executeContext, environment);
    }

    private void buildGlobalSetting(FlinkExecuteContext executeContext, SimpleJobFlow startJobFlow) {

       executeContext.getEnvironment().setParallelism(executeContext.getJobSetting().getSpeed().getChannel());

    }

    private void initLoggerSetting(ExecuteContext executeContext) {
        LoggerHelper.init(executeContext.getJobSetting().getLog());
    }


    private FlinkExecuteContext buildFlinkExecuteContext(ExecuteContext executeContext, StreamExecutionEnvironment environment) {
        FlinkExecuteContext flinkExecuteContext = new FlinkExecuteContext();
        flinkExecuteContext.setEnvironment(environment);
        flinkExecuteContext.setJobExecutionListeners(executeContext.getJobExecutionListeners());
        flinkExecuteContext.setJobSetting(executeContext.getJobSetting());


        //设置检查点
        if(flinkExecuteContext.getEnvironment() instanceof LocalStreamEnvironment) {
            if(StringUtils.isNotEmpty(executeContext.getJobSetting().getRestore().getSavepointRestorePath())){
                ((LocalStreamEnvironment) flinkExecuteContext.getEnvironment()).setSettings(SavepointRestoreSettings.forPath(executeContext.getJobSetting().getRestore().getSavepointRestorePath()));
            }
        }

        return flinkExecuteContext;
    }

    private StreamExecutionEnvironment buildStreamExecutionEnvironment(ExecuteContext executeContext) {
        StreamExecutionEnvironment environment =
                StringUtils.isNotEmpty(executeContext.getJobSetting().getMonitorUrls()) ?
                StreamExecutionEnvironment.getExecutionEnvironment():
                new LocalStreamEnvironment();
        return environment;
    }
}
