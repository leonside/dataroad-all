package com.leonside.dataroad.common.spi;


import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.extension.SPI;

/**
 * Created by Administrator on 2020/10/13.
 */
@SPI
public interface ItemWriter<T extends ExecuteContext, IN> extends Component {

    void write(T executeContext, IN items);

}
