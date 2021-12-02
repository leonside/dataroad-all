package com.leonside.dataroad.core.component;

import com.leonside.dataroad.common.context.ExecuteContext;

import java.util.Map;

/**
 * @author leon
 */
public interface ComponentInitialization<T extends ExecuteContext> {

    void initialize(ExecuteContext executeContext, Map<String, Object> parameter);

}
