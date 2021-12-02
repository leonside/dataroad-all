package com.leonside.dataroad.common.exception;

/**
 * @author leon
 */
public class JobException extends RuntimeException {

    public JobException() {
        super();
    }

    public JobException(String message) {
        super(message);
    }

    public JobException(String message, Throwable cause) {
        super(message, cause);
    }

}
