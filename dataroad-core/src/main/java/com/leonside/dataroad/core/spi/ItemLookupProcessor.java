package com.leonside.dataroad.core.spi;

import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.extension.SPI;
import com.leonside.dataroad.common.spi.ItemProcessor;

/**
 * @author leon
 */
@SPI
public interface ItemLookupProcessor <T extends ExecuteContext,IN,OUT> extends ItemProcessor<T,IN,OUT> {
}
