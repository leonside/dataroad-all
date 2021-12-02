package com.leonside.dataroad.common.context;

import lombok.Data;

import java.io.Serializable;

/**
 * @author leon
 */
@Data
public class SpeedConfig implements Serializable {

    private long bytes = Long.MAX_VALUE;
    private int channel = 1;
    private int readerChannel = -1;
    private int writerChannel = -1;

    public static SpeedConfig defaultConfig() {
        return new SpeedConfig();
    }
//    private boolean rebalance = false;
}
