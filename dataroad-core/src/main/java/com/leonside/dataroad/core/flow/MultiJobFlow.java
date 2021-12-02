package com.leonside.dataroad.core.flow;

import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.exception.JobFlowException;
import com.leonside.dataroad.common.spi.Component;
import com.leonside.dataroad.core.spi.ItemDeciderProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author leon
 */
public class MultiJobFlow extends JobFlow<ExecuteContext> {

    private ItemDeciderProcessor itemDeciderProcessor;

    public MultiJobFlow(ItemDeciderProcessor itemDeciderProcessor) {
        super(new Task(itemDeciderProcessor));
        this.itemDeciderProcessor = itemDeciderProcessor;
    }

    @Override
    public Optional<?> doExecute(JobFlow multiJobFlow, ExecuteContext executeContext, Function<JobFlow, ?> parentOutput) throws Exception {
        Optional deciderOutput = multiJobFlow.getTask().execute(executeContext, parentOutput.apply(multiJobFlow));

        List multiJobFlowOutput = new ArrayList<>();

        if(deciderOutput.isPresent()){
            ((Map)deciderOutput.get()).entrySet().forEach(it->{
                try {
                    Map.Entry entry = (Map.Entry) it;
                    Map<Predicate, JobFlow> jobFlowDeciders = itemDeciderProcessor.getJobFlowDeciders();
                    Optional<?> sideOutput = jobFlowDeciders.get(entry.getKey()).execute(executeContext, (Function<JobFlow, ?>) o -> entry.getValue());

                    if(sideOutput.isPresent()){
                        multiJobFlowOutput.add(sideOutput.get());
                    }
                } catch (Exception e) {
                    throw new JobFlowException("multi job flow execute exception",e);
                }
            });
        }
        deciderOutput.ifPresent(o -> executeContext.putJobFlowOutput(task.getComponentName(), multiJobFlowOutput));

        return deciderOutput;

    }

    @Override
    public String toString() {
        return "MultiJobFlow{" +
                "jobFlowDeciders=" + itemDeciderProcessor.getJobFlowDeciders() +
                '}';
    }

    public static <T extends Component> MultiJobFlow of(ItemDeciderProcessor itemDeciderProcessor){
        return new MultiJobFlow(itemDeciderProcessor);
    }

}
