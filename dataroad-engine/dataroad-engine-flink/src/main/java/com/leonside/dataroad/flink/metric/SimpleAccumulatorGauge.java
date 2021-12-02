
package com.leonside.dataroad.flink.metric;

import org.apache.flink.api.common.accumulators.Accumulator;
import org.apache.flink.metrics.Gauge;

import java.io.Serializable;

/**
 *
 */
public class SimpleAccumulatorGauge<T extends Serializable> implements Gauge<T> {

    private Accumulator<T, T> accumulator;

    public SimpleAccumulatorGauge(Accumulator<T, T> accumulator) {
        this.accumulator = accumulator;
    }

    @Override
    public T getValue() {
        return accumulator.getLocalValue();
    }
}
