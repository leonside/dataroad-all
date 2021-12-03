package com.leonside.dataroad.flink;

import com.leonside.dataroad.core.Job;
import com.leonside.dataroad.core.flow.SimpleJobFlow;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author leon
 */
//todo  SPI扩展实现
public class FlinkJob implements Job {

    private SimpleJobFlow simpleJobFlow;

    private StreamExecutionEnvironment environment;

    private FlinkExecuteContext flinkExecuteContext;

    public FlinkJob(FlinkExecuteContext context, SimpleJobFlow startJobFlow) {
        this.simpleJobFlow = startJobFlow;
        this.flinkExecuteContext = context;
        this.environment = context.getEnvironment();
    }

    @Override
    public void execute() throws Exception {

        simpleJobFlow.execute(flinkExecuteContext);

        environment.execute(flinkExecuteContext.getJobSetting().getName());
    }

}
