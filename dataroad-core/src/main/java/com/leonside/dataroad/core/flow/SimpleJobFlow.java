package com.leonside.dataroad.core.flow;

import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.spi.Component;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author leon
 */
public class SimpleJobFlow extends JobFlow<ExecuteContext> {

    public SimpleJobFlow(Task task) {
        super(task);
    }

    @Override
    public Optional<?> doExecute(JobFlow simpleJobFlow, ExecuteContext context, Function<JobFlow, ?> parentOutput) throws Exception {

        Optional<?> output = simpleJobFlow.getTask().execute(context, parentOutput.apply(simpleJobFlow));

        output.ifPresent(o -> context.putJobFlowOutput(simpleJobFlow.getTask().getComponentName(), o));

        return output;
    }

    public static <T extends Component> SimpleJobFlow of(T component){
        return new SimpleJobFlow(new Task(component));
    }

    @Override
    public String toString() {
        return "SimpleJobFlow{" +
                "task=" + task +
                "child=" + getChildren() +
                '}';
    }
}
