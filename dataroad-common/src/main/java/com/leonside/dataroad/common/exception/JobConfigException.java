package com.leonside.dataroad.common.exception;

/**
 * @author leon
 */
public class JobConfigException extends JobException{
    public JobConfigException() {
    }

    public JobConfigException(String message) {
        super(message);
    }

    public JobConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
