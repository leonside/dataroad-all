package com.leonside.dataroad.core.aggregations.config;

import com.leonside.dataroad.common.config.ConfigKey;
import com.leonside.dataroad.common.config.Validation;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author leon
 */
@Data
public class TumblingWindowConfig extends BaseWindowConfig  {

    public long timeSize;

    public TimeUnit timeUnit = TimeUnit.SECONDS;

    public BaseWindowConfig.WindowTimeType timeType = WindowTimeType.process;

    public String eventTimeColumn;

    public int outOfOrderness = 0;

    public TumblingWindowConfig(Map<String, Object> parameter) {
        super(parameter);
    }

    public TumblingWindowConfig(){
        super(null);
    }

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

    @Override
    public Class<? extends ConfigKey> bindConfigKey() {
        return TumblingWindowConfigKey.class;
    }
}
