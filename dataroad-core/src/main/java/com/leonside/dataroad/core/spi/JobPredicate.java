package com.leonside.dataroad.core.spi;

import com.leonside.dataroad.common.context.ExecuteContext;
import com.leonside.dataroad.common.extension.SPI;

import java.io.Serializable;

/**
 * @author leon
 */
@SPI
@FunctionalInterface
public interface JobPredicate<T extends ExecuteContext, ROW> extends Serializable {

    public static JobPredicate TRUE_PREDICATE = (context, o) -> true;

    public static JobPredicate FALSE_PREDICATE = (context, o) -> false;

    boolean test(T t, ROW row);

}
