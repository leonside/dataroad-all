package com.leonside.dataroad.core.component;

import com.leonside.dataroad.common.context.ExecuteContext;

import java.util.Map;

/**
 * @author leon
 */
public interface ComponentInitialization<T extends ExecuteContext> extends Validation {

    void initialize(T executeContext, Map<String, Object> parameter);

}
