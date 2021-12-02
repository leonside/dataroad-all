package com.leonside.dataroad.core.flow;

import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.exception.JobFlowException;
import com.leonside.dataroad.common.spi.Component;
import com.leonside.dataroad.common.spi.ItemProcessor;
import com.leonside.dataroad.common.spi.ItemReader;
import com.leonside.dataroad.common.spi.ItemWriter;
import com.leonside.dataroad.common.utils.Asserts;

import java.io.Serializable;
import java.util.Optional;

/**
 * @author leon
 */
public class Task< CTX extends ExecuteContext, IN, OUT> implements Serializable {

    private Component component;

    public Task(Component component) {
        this.component = component;
        Asserts.notNull(component, "component can not be null");
    }

    public Component getComponent(){
        return component;
    }

    public String getComponentName(){
        return component.getName();
    }

    public Optional<OUT> execute(CTX executeContext, IN in) throws Exception {

        if(component instanceof ItemReader){

            return (Optional<OUT>) Optional.of(((ItemReader) component).read(executeContext));

        }else if (component instanceof ItemWriter){
            //todo
            ((ItemWriter) component).write(executeContext, in);
            return Optional.empty();

        }else if(component instanceof ItemProcessor){

            return (Optional<OUT>) Optional.of(((ItemProcessor) component).process(executeContext, in));

        }else{
            throw new JobFlowException("unkown component type [" + component.getName() + "]");
        }
    }

    @Override
    public String toString() {
        return "Task{" +
                "component=" + component.getName() +
                '}';
    }
}
