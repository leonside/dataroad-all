package com.leonside.dataroad.common.spi;

import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.extension.SPI;

/**
 * @author leon
 */
@SPI
public interface ItemUnionProcessor <T extends ExecuteContext,IN,OUT> extends ItemProcessor<T,IN,OUT> {

     void initializeUnionFlowIndex(Integer[] indexs);
}
