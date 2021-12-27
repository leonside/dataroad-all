package com.leonside.dataroad.common.context;

import lombok.Data;

import java.io.Serializable;

/**
 * @author leon
 */
@Data
public class RestoreConfig implements Serializable {

    /**
     * 是否启用Checkpointing
     */
    private boolean isRestore = false;
    private boolean isStream = false;

    /**
     * 设置restore列名
     */
    private String restoreColumnName;
    /**
     * 设置restore列类型，当为空则获取reader中配置的columnType
     */
    private String restoreColumnType;
    private int restoreColumnIndex = -1;
    private long maxRowNumForCheckpoint = 10000;


    /**
     * 设置StateBackend checkpointDataUri
     */
    private String savepointPath;
    /**
     * 设置SavepointRestorePath
     */
    private String savepointRestorePath;
    /**
     * Checkpointing间隔时间
     */
    private Integer savepointInterval;

    public static RestoreConfig defaultConfig() {
        return new RestoreConfig();
    }

    public void setIsRestore(boolean restore) {
        isRestore = restore;
    }

    public void setIsStream(boolean stream) {
        isStream = stream;
    }
}
