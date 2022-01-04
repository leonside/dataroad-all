package com.leonside.dataroad.core.spi;

import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.extension.SPI;
import com.leonside.dataroad.common.spi.ItemProcessor;
import com.leonside.dataroad.core.aggregations.AggerationEnum;
import com.leonside.dataroad.core.aggregations.config.BaseWindowConfig;
import com.leonside.dataroad.core.builder.AggerationBuilder;
import com.leonside.dataroad.core.component.ComponentInitialization;

import java.util.List;
import java.util.Map;

/**
 * @author leon
 */
@SPI
public interface ItemAggregationProcessor<T extends ExecuteContext,IN,OUT> extends ItemProcessor<T,IN,OUT> {

//    void initialize(AggerationBuilder.Window window, Map<String, List<AggerationEnum>> aggerations);

    void initialize(BaseWindowConfig baseWindowConfig);

}
