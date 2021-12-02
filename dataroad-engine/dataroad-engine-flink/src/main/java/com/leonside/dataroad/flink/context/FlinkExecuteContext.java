package com.leonside.dataroad.flink.context;

import com.leonside.dataroad.common.context.ExecuteContext;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * @author leon
 */
public class FlinkExecuteContext extends ExecuteContext {

    private transient StreamExecutionEnvironment environment;

    public StreamExecutionEnvironment getEnvironment() {
        return environment;
    }

    public void setEnvironment(StreamExecutionEnvironment environment) {
        this.environment = environment;
    }
}
