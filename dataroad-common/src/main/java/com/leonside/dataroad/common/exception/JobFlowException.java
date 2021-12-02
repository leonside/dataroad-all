package com.leonside.dataroad.common.exception;

/**
 * @author leon
 */
public class JobFlowException extends JobException{
    public JobFlowException() {
    }

    public JobFlowException(String message) {
        super(message);
    }

    public JobFlowException(String message, Throwable cause) {
        super(message, cause);
    }
}
