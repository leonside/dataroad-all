package com.leonside.dataroad.common.spi;


import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.extension.SPI;

/**
 * Created by Administrator on 2020/10/21.
 */
@SPI
public interface ItemProcessor<T extends ExecuteContext,IN,OUT> extends Component {

    public OUT process(T executeContext, IN in);

}