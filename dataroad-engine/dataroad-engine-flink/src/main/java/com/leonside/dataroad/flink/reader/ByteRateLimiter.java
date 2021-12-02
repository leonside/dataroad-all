
package com.leonside.dataroad.flink.reader;

import com.google.common.util.concurrent.RateLimiter;
import com.leonside.dataroad.flink.metric.AccumulatorCollector;
import com.leonside.dataroad.flink.metric.Metrics;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 */
public class ByteRateLimiter {

    private final static Logger LOG = LoggerFactory.getLogger(ByteRateLimiter.class);

    public static final int MIN_RECORD_NUMBER_UPDATE_RATE = 1000;

    private RateLimiter rateLimiter;

    private double expectedBytePerSecond;

    private AccumulatorCollector accumulatorCollector;

    private ScheduledExecutorService scheduledExecutorService;

    public ByteRateLimiter(AccumulatorCollector accumulatorCollector, double expectedBytePerSecond) {
        double initialRate = 1000.0;
        this.rateLimiter = RateLimiter.create(initialRate);
        this.expectedBytePerSecond = expectedBytePerSecond;
        this.accumulatorCollector = accumulatorCollector;

        ThreadFactory threadFactory = new BasicThreadFactory
                .Builder()
                .namingPattern("ByteRateCheckerThread-%d")
                .daemon(true)
                .build();
        scheduledExecutorService = new ScheduledThreadPoolExecutor(1, threadFactory);
    }

    public void start(){
        scheduledExecutorService.scheduleAtFixedRate(this::updateRate,0, 1000L, TimeUnit.MILLISECONDS);
    }

    public void stop(){
        if(scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }
    }

    public void acquire() {
        rateLimiter.acquire();
    }

    private void updateRate(){
        long totalBytes = accumulatorCollector.getAccumulatorValue(Metrics.READ_BYTES);
        long thisRecords = accumulatorCollector.getLocalAccumulatorValue(Metrics.NUM_READS);
        long totalRecords = accumulatorCollector.getAccumulatorValue(Metrics.NUM_READS);

        BigDecimal thisWriteRatio = BigDecimal.valueOf(totalRecords == 0 ? 0 : thisRecords / (double) totalRecords);

        if (totalRecords > MIN_RECORD_NUMBER_UPDATE_RATE && totalBytes != 0
                && thisWriteRatio.compareTo(BigDecimal.ZERO) != 0) {
            double bpr = totalBytes / (double)totalRecords;
            double permitsPerSecond = expectedBytePerSecond / bpr * thisWriteRatio.doubleValue();
            rateLimiter.setRate(permitsPerSecond);
        }
    }
}
