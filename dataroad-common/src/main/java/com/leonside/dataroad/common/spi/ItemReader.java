package com.leonside.dataroad.common.spi;


import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.extension.SPI;

/**
 * Created by Administrator on 2020/10/13.
 */
@SPI
public interface ItemReader<T extends ExecuteContext, R> extends Component {

    R read(T executeContext) throws Exception;

}
