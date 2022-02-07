package com.leonside.dataroad.flink.predicate;

import com.leonside.dataroad.core.component.ComponentInitialization;
import com.leonside.dataroad.core.component.ComponentNameSupport;
import com.leonside.dataroad.core.spi.JobPredicate;
import com.leonside.dataroad.flink.config.ScriptExpressionConfig;
import com.leonside.dataroad.flink.context.FlinkExecuteContext;
import org.apache.flink.types.Row;

/**
 * @author leon
 */
public class TrueExpressionPredicate extends ComponentNameSupport implements ComponentInitialization<FlinkExecuteContext,ScriptExpressionConfig>, JobPredicate<FlinkExecuteContext, Row> {

    public static final String COMPONENT_ID = "trueExpressionPredicate";

    @Override
    public boolean test(FlinkExecuteContext executeContext, Row row) {

        return true;
    }

    @Override
    public void doInitialize(FlinkExecuteContext executeContext, ScriptExpressionConfig config) {
     }

}
