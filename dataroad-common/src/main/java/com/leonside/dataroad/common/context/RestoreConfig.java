package com.leonside.dataroad.common.context;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.leonside.dataroad.common.config.Validation;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * @author leon
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RestoreConfig implements Serializable, Validation {

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


    @Override
    public boolean validate(){
        if (isRestore )  {
            if(StringUtils.isEmpty(savepointPath)){
                throw new IllegalArgumentException("If Restore is true, the savepoint Path parameter must be configured");
            }

            if(StringUtils.isEmpty(restoreColumnName)){
                throw new IllegalArgumentException("If Restore is true, the restoreColumnName parameter must be configured");
            }
            if(StringUtils.isEmpty(restoreColumnType)){
                throw new IllegalArgumentException("If Restore is true, the restoreColumnType parameter must be configured");
            }
        }
        return true;
    }

}
