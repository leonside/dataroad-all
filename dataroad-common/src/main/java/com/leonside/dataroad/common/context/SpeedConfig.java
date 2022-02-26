package com.leonside.dataroad.common.context;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * @author leon
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpeedConfig implements Serializable {

    private long bytes = Long.MAX_VALUE;

    public static final int CHANNEL_DEFAULT = 1;
    private int channel = -1;
    private int readerChannel = -1;
    private int writerChannel = -1;

    public static SpeedConfig defaultConfig() {
        return new SpeedConfig();
    }
//    private boolean rebalance = false;
}
