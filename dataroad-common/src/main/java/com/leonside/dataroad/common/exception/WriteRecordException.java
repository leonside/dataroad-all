package com.leonside.dataroad.common.exception;

/**
 * @author leon
 */
public class WriteRecordException extends JobException{

    private int columnIdx;

    public WriteRecordException() {
    }

    public WriteRecordException(String message) {
        super(message);
    }

    public WriteRecordException(String message, Throwable cause) {
        super(message, cause);
    }

    public int getColumnIdx() {
        return columnIdx;
    }

    public WriteRecordException(int columnIdx, String message) {
        super(message);
        this.columnIdx = columnIdx;
    }

    public WriteRecordException(int columnIdx,String message, Throwable cause) {
        super(message, cause);
        this.columnIdx = columnIdx;
    }
}
