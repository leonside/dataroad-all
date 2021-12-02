package com.leonside.dataroad.common.context;

import lombok.Data;

import java.io.Serializable;

/**
 * @author leon
 */
@Data
public class RestoreConfig implements Serializable {

    private boolean isRestore = false;
    private boolean isStream = false;

    private String restoreColumnName;
    private String restoreColumnType;
    private int restoreColumnIndex = -1;
    private long maxRowNumForCheckpoint = 10000;

    public static RestoreConfig defaultConfig() {
        return new RestoreConfig();
    }
}
