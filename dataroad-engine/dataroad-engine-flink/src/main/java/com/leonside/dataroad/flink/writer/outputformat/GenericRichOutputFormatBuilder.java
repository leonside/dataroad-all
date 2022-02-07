
package com.leonside.dataroad.flink.writer.outputformat;

import com.leonside.dataroad.common.constant.JobCommonConstant;
import com.leonside.dataroad.common.context.RestoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The builder of RichOutputFormat
 *
 */
public abstract class GenericRichOutputFormatBuilder<T extends GenericRichOutputFormat, R extends GenericRichOutputFormatBuilder> {
    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    protected T format;

//    public R setDirtyPath(String dirtyPath) {
//        format.setDirtyPath(dirtyPath);
//        return (R)this;
//    }

//    public R setDirtyHadoopConfig(Map<String,Object> dirtyHadoopConfig) {
//        format.setDirtyHadoopConfig(dirtyHadoopConfig);
//        return (R)this;
//    }

    public R setRestore(RestoreConfig restore){
        format.setRestoreConfig(restore);
        return (R)this;
    }

//    public R setSrcCols(List<String> srcCols) {
//        format.setSrcFieldNames(srcCols);
//        return (R)this;
//    }

    public R setErrors(Integer errors) {
        format.errors = errors;
        return (R)this;
    }

    public R setErrorRatio(Double errorRatio) {
        format.errorRatio = errorRatio;
        return (R)this;
    }

    public R setMonitorUrls(String monitorUrl) {
        format.monitorUrl = monitorUrl;
        return (R)this;
    }

    public R setBatchInterval(int batchInterval) {
        format.batchInterval = batchInterval;
        return (R)this;
    }

    public R setRestoreConfig(RestoreConfig restoreConfig){
        format.restoreConfig = restoreConfig;
        return (R)this;
    }

//    public R setInitAccumulatorAndDirty(boolean initAccumulatorAndDirty) {
//        this.format.initAccumulatorAndDirty = initAccumulatorAndDirty;
//        return (R)this;
//    }
//
//    protected R notSupportBatchWrite(String writerName) {
//        if (this.format.getBatchInterval() > 1) {
//            throw new IllegalArgumentException(writerName + "不支持批量写入");
//        }
//        return (R)this;
//    }

    /**
     * Check the value of parameters
     */
    protected abstract void checkFormat();

    public GenericRichOutputFormat finish() {
        checkFormat();

        /**
         * 200000条限制的原因：
         * 按照目前的使用情况以及部署配置，假设写入字段数量平均为50个，一个单slot的TaskManager内存为1G，
         * 在不考虑各插件批量写入对内存特殊要求并且只考虑插件缓存这么多条数据的情况下，batchInterval为400000条时出现fullGC，
         * 为了避免fullGC以及OOM，并且保证batchInterval有足够的配置空间，取最大值的一半200000。
         */
        if (this.format.getBatchInterval() > JobCommonConstant.MAX_BATCH_SIZE) {
            throw new IllegalArgumentException("批量写入条数必须小于[200000]条");
        }

        return format;
    }
}
