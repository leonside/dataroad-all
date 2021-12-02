
package com.leonside.dataroad.flink.restore;

import org.apache.flink.api.common.accumulators.LongCounter;

import java.io.Serializable;
import java.util.Map;

/**
 */
public class FormatState implements Serializable {

    private int numOfSubTask;

    private Object state;

    /**
     * store metric info
     */
    private Map<String, LongCounter> metric;

    private long numberRead;

    private long numberWrite;

    private String jobId;

    private int fileIndex = -1;

    public FormatState() {
    }

    public FormatState(int numOfSubTask, Object state) {
        this.numOfSubTask = numOfSubTask;
        this.state = state;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public int getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(int fileIndex) {
        this.fileIndex = fileIndex;
    }

    public long getNumberRead() {
        return numberRead;
    }

    public void setNumberRead(long numberRead) {
        this.numberRead = numberRead;
    }

    public long getNumberWrite() {
        return numberWrite;
    }

    public void setNumberWrite(long numberWrite) {
        this.numberWrite = numberWrite;
    }

    public int getNumOfSubTask() {
        return numOfSubTask;
    }

    public void setNumOfSubTask(int numOfSubTask) {
        this.numOfSubTask = numOfSubTask;
    }

    public Object getState() {
        return state;
    }

    public void setState(Object state) {
        this.state = state;
    }

    public Map<String, LongCounter> getMetric() {
        return metric;
    }

    public long getMetricValue(String key) {
        if (metric != null) {
            return metric.get(key).getLocalValue();
        }
        return 0;
    }

    public void setMetric(Map<String, LongCounter> metric) {
        this.metric = metric;
    }

    @Override
    public String toString() {
        return "FormatState{" +
                "numOfSubTask=" + numOfSubTask +
                ", state=" + state +
                ", metric=" + metric +
                ", numberRead=" + numberRead +
                ", numberWrite=" + numberWrite +
                ", jobId='" + jobId + '\'' +
                ", fileIndex=" + fileIndex +
                '}';
    }
}
