
package com.leonside.dataroad.flink.metric;

import org.apache.flink.api.common.accumulators.LongCounter;
import org.apache.flink.metrics.Counter;
import org.apache.flink.metrics.Meter;
import org.apache.flink.metrics.View;

/**
 * A MeterView provides an average rate of events per second over a given time period.
 *
 * <p>The primary advantage of this class is that the rate is neither updated by the computing thread nor for every event.
 * Instead, a history of counts is maintained that is updated in regular intervals by a background thread. From this
 * history a rate is derived on demand, which represents the average rate of events over the given time span.
 *
 * <p>Setting the time span to a low value reduces memory-consumption and will more accurately report short-term changes.
 * The minimum value possible is {@link View#UPDATE_INTERVAL_SECONDS}.
 * A high value in turn increases memory-consumption, since a longer history has to be maintained, but will result in
 * smoother transitions between rates.
 *
 * <p>The events are counted by a {@link Counter}.
 *
 * @author toutian
 */
public class SimpleLongCounterMeterView implements Meter, View {
	/** The underlying counter maintaining the count. */
	private final LongCounter counter;
	/** The time-span over which the average is calculated. */
	private final int timeSpanInSeconds;
	/** Circular array containing the history of values. */
	private final long[] values;
	/** The index in the array for the current time. */
	private int time = 0;
	/** The last rate we computed. */
	private double currentRate = 0;

	public SimpleLongCounterMeterView(int timeSpanInSeconds) {
		this(new LongCounter(), timeSpanInSeconds);
	}

	public SimpleLongCounterMeterView(LongCounter counter, int timeSpanInSeconds) {
		this.counter = counter;
		this.timeSpanInSeconds = timeSpanInSeconds - (timeSpanInSeconds % UPDATE_INTERVAL_SECONDS);
		this.values = new long[this.timeSpanInSeconds / UPDATE_INTERVAL_SECONDS + 1];
	}

	@Override
	public void markEvent() {
		this.counter.add(1);
	}

	@Override
	public void markEvent(long n) {
		this.counter.add(n);
	}

	@Override
	public long getCount() {
		return counter.getLocalValue();
	}

	@Override
	public double getRate() {
		return currentRate;
	}

	@Override
	public void update() {
		time = (time + 1) % values.length;
		values[time] = counter.getLocalValue();
		currentRate =  ((double) (values[time] - values[(time + 1) % values.length]) / timeSpanInSeconds);
	}
}
