package com.leonside.dataroad.core.spi;

import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.extension.SPI;
import com.leonside.dataroad.core.Job;
import com.leonside.dataroad.core.flow.SimpleJobFlow;

@SPI
public interface JobEngineCreator {

    Job create(ExecuteContext executeContext, SimpleJobFlow startJobFlow);
}
