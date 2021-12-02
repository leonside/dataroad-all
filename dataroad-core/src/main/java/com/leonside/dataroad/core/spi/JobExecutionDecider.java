package com.leonside.dataroad.core.spi;

import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.extension.SPI;
import com.leonside.dataroad.core.predicate.ExecuteStatus;

import java.io.Serializable;

/**
 * @author leon
 */
@SPI
public interface JobExecutionDecider<T extends ExecuteContext, IN> extends Serializable {

    ExecuteStatus decide(T context, IN in);

}

