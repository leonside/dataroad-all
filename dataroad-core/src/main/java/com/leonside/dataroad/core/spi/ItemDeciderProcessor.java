package com.leonside.dataroad.core.spi;

import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.extension.SPI;
import com.leonside.dataroad.common.spi.ItemProcessor;
import com.leonside.dataroad.core.flow.JobFlow;

import java.util.Map;

/**
 * @author leon
 */
@SPI
public interface ItemDeciderProcessor<T extends ExecuteContext,IN,OUT> extends ItemProcessor<T,IN,OUT> {

    Map<JobPredicate, JobFlow> getJobFlowDeciders();

    void initialize(Map<JobPredicate, JobFlow> predicateJobFlowMap);
}
