package com.leonside.dataroad.flink.inputformat;

import com.google.common.base.Preconditions;
import com.leonside.dataroad.common.context.RestoreConfig;
import com.leonside.dataroad.core.component.Validation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author leon
 */
public abstract class GenericRichInputFormatBuilder<T extends GenericRichInputFormat, R extends GenericRichInputFormatBuilder> implements Validation {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected T format;

    public R setMonitorUrls(String monitorUrls) {
        format.monitorUrls = monitorUrls;
        return (R)this;
    }

    public R setBytes(long bytes) {
        format.bytes = bytes;
        return (R) this;
    }

    public R setRestoreConfig(RestoreConfig restoreConfig){
        format.restoreConfig = restoreConfig;
        return (R) this;
    }


    public T finish() {
        Preconditions.checkNotNull(format);

        validate();

        return format;
    }

}
