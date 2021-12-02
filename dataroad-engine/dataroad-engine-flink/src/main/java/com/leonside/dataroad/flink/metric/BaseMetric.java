package com.leonside.dataroad.flink.metric;

import org.apache.flink.api.common.accumulators.LongCounter;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.metrics.MetricGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BaseMetric {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    private Long delayPeriodMill = 20000L;

    private MetricGroup flinkxOutput;

    private final Map<String, LongCounter> metricCounters = new HashMap<>();

    public BaseMetric(RuntimeContext runtimeContext) {
        flinkxOutput = runtimeContext.getMetricGroup().addGroup(Metrics.METRIC_GROUP_KEY_FLINKX, Metrics.METRIC_GROUP_VALUE_OUTPUT);
    }

    public void addMetric(String metricName, LongCounter counter){
        addMetric(metricName, counter, false);
    }

    public void addMetric(String metricName, LongCounter counter, boolean meterView){
        metricCounters.put(metricName, counter);
        flinkxOutput.gauge(metricName, new SimpleAccumulatorGauge<>(counter));
        if (meterView){
            flinkxOutput.meter(metricName + Metrics.SUFFIX_RATE, new SimpleLongCounterMeterView(counter, 20));
        }
    }

    public Map<String, LongCounter> getMetricCounters() {
        return metricCounters;
    }

    public void waitForReportMetrics() {
        try {
            Thread.sleep(delayPeriodMill);
        } catch (InterruptedException e){
            LOG.warn("Task thread is interrupted");
        }
    }
}