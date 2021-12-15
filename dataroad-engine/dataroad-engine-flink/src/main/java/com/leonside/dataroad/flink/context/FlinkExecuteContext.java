package com.leonside.dataroad.flink.context;

import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.domain.MetaColumn;
import com.leonside.dataroad.core.flow.JobFlow;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.List;

/**
 * @author leon
 */
public class FlinkExecuteContext extends ExecuteContext {

    private transient StreamExecutionEnvironment environment;

    private JobFlow startJobFlow;

    private List<MetaColumn> startMetaColumn;

    public StreamExecutionEnvironment getEnvironment() {
        return environment;
    }

    public JobFlow getStartJobFlow() {
        return startJobFlow;
    }

    public void setStartJobFlow(JobFlow startJobFlow) {
        this.startJobFlow = startJobFlow;
    }

    public List<MetaColumn> getStartMetaColumn() {
        return startMetaColumn;
    }

    public void setStartMetaColumn(List<MetaColumn> startMetaColumn) {
        this.startMetaColumn = startMetaColumn;
    }

    public void setEnvironment(StreamExecutionEnvironment environment) {
        this.environment = environment;
    }
}
