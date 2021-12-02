package com.leonside.dataroad.common.exception;

/**
 * @author leon
 */
public class WriteRecordException extends JobException{
    public WriteRecordException() {
    }

    public WriteRecordException(String message) {
        super(message);
    }

    public WriteRecordException(String message, Throwable cause) {
        super(message, cause);
    }
}
