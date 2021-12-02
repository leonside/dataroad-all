package com.leonside.dataroad.core.flow;

import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.utils.Asserts;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author leon
 */
public abstract class JobFlow<T extends ExecuteContext> implements Serializable {

    protected JobFlow parent;

    protected JobFlow children;

    protected Task task;

    public JobFlow(Task task) {
        this.task = task;
        Asserts.notNull(task, "task can not be null.");
    }

    public Object getParentOutput(T context){
        return parent == null ? null : context.getJobFlowOutput(parent.getTask().getComponentName());
    }

    public Task getTask() {
        return task;
    }

    public JobFlow getRootParent(){
        JobFlow parent = getParent();
        if (parent == null){
            return this;
        }else{
            return parent.getRootParent();
        }
    }

    public JobFlow getParent() {
        return parent;
    }

    public void setParent(JobFlow parent) {
        this.parent = parent;
    }

    public JobFlow getChildren() {
        return children;
    }

    public void addChildren(JobFlow children) {
        this.children = children;
        children.setParent(this);
    }

    public Optional<?> execute(T executeContext) throws Exception {
        return this.execute(executeContext, (Function<JobFlow, ?>) o -> o.getParentOutput(executeContext));
    }

    public Optional<?> execute(T executeContext, Function<JobFlow, ?>  parentOutput) throws Exception {
        Optional<?> output = doExecute(this, executeContext, parentOutput);

        //execute next
        if(this.getChildren() != null){
            output = this.getChildren().execute(executeContext);
        }

        return output;
    }

    public abstract Optional<?> doExecute(JobFlow simpleJobFlow, ExecuteContext context, Function<JobFlow, ?> parentOutput) throws Exception ;



}
