package com.leonside.dataroad.core.spi;

import com.leonside.dataroad.common.config.Options;
import com.leonside.dataroad.common.context.ComponentHolder;
import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.context.JobSetting;
import com.leonside.dataroad.common.extension.SPI;
import com.leonside.dataroad.common.spi.JobExecutionListener;
import com.leonside.dataroad.core.Job;
import com.leonside.dataroad.core.flow.SimpleJobFlow;

import java.util.List;

@SPI
public interface JobEngineProvider<T extends ExecuteContext> {

    Job createJob(T executeContext, SimpleJobFlow startJobFlow);

    T createExecuteContext(JobSetting jobSetting, List<ComponentHolder> componentHolders, Options options);

}
