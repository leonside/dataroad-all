package com.leonside.dataroad.core.predicate;

import com.leonside.dataroad.common.constant.JobConfigKeyConstants;
import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.core.spi.JobPredicate;

import java.io.Serializable;

public class OtherwisePredicate implements JobPredicate<ExecuteContext, Object>, Serializable {

    private String name = JobConfigKeyConstants.KEY_PREDICATE_OTHERWISE;

    @Override
    public boolean test(ExecuteContext context, Object row) {
        return true;
    }

}