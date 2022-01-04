package com.leonside.dataroad.core.aggregations.config;

import com.leonside.dataroad.core.component.Validation;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author leon
 */
@Data
public class TumblingWindowConfig extends BaseWindowConfig implements Validation {

    public long timeSize;

    public TimeUnit timeUnit = TimeUnit.SECONDS;

    public BaseWindowConfig.WindowTimeType timeType = WindowTimeType.process;

    public String eventTimeColumn;

    public int outOfOrderness = 0;

    @Override
    public String windowComponentName() {
        return "tumblingWindowAgg";
    }

    @Override
    public boolean validate() {

        if(timeType == WindowTimeType.event && StringUtils.isEmpty(eventTimeColumn)){
            throw new IllegalArgumentException("eventTimeColumn cannot be empty when timeType is WindowTimetype.event");
        }

        return super.validate();
    }
}
