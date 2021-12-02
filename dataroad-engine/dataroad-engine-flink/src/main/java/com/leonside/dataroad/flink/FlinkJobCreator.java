package com.leonside.dataroad.flink;

import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.core.Job;
import com.leonside.dataroad.core.spi.JobEngineCreator;
import com.leonside.dataroad.core.flow.SimpleJobFlow;
import com.leonside.dataroad.core.support.LoggerHelper;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author leon
 */
public class FlinkJobCreator implements JobEngineCreator {
    @Override
    public Job create(ExecuteContext executeContext, SimpleJobFlow startJobFlow) {

        //checkpoint\configuration\Parallelism\isolation classloader
        StreamExecutionEnvironment environment = buildStreamExecutionEnvironment(executeContext);

        FlinkExecuteContext flinkExecuteContext = buildFlinkExecuteContext(executeContext, environment);

        buildGlobalSetting(flinkExecuteContext, startJobFlow);

        initLoggerSetting(executeContext);
        //metric??
//
//        buildJobExecuteListener();

        return new FlinkJob(flinkExecuteContext, startJobFlow, environment);
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
        return flinkExecuteContext;
    }

    private StreamExecutionEnvironment buildStreamExecutionEnvironment(ExecuteContext executeContext) {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        return environment;
    }
}
