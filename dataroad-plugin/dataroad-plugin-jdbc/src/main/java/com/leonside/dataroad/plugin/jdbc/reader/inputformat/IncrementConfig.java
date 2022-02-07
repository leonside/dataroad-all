package com.leonside.dataroad.plugin.jdbc.reader.inputformat;

import java.io.Serializable;

/**
 * @author jiangbo
 * @explanation
 * @date 2019/3/29
 */
public class IncrementConfig implements Serializable {

    /**
     * 是否为增量任务
     */
    public boolean increment;

    /**
     * 是否为增量轮询
     */
    public boolean polling;

    /**
     * 用于标记是否保存endLocation位置的一条或多条数据
     *  true：不保存
     *  false(默认)：保存
     *  某些情况下可能出现最后几条数据被重复记录的情况，可以将此参数配置为true
     */
    public boolean useMaxFunc;

    public int columnIndex;

    public String columnName;

    public String columnType;

    public String startLocation;

    /**
     * 轮询时间间隔
     */
    public long pollingInterval;

    /**
     * 发送查询累加器请求的间隔时间
     */
    public int requestAccumulatorInterval;

    public int getRequestAccumulatorInterval() {
        return requestAccumulatorInterval;
    }

    public void setRequestAccumulatorInterval(int requestAccumulatorInterval) {
        this.requestAccumulatorInterval = requestAccumulatorInterval;
    }

    public boolean isIncrement() {
        return increment;
    }

    public void setIncrement(boolean increment) {
        this.increment = increment;
    }

    public boolean isPolling() {
        return polling;
    }

    public void setPolling(boolean polling) {
        this.polling = polling;
    }

    public boolean isUseMaxFunc() {
        return useMaxFunc;
    }

    public void setUseMaxFunc(boolean useMaxFunc) {
        this.useMaxFunc = useMaxFunc;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public long getPollingInterval() {
        return pollingInterval;
    }

    public void setPollingInterval(long pollingInterval) {
        this.pollingInterval = pollingInterval;
    }
}
