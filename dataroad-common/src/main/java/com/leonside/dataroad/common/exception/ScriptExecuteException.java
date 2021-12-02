package com.leonside.dataroad.common.exception;

/**
 * @author leon
 */
public class ScriptExecuteException extends JobException{
    public ScriptExecuteException() {
    }

    public ScriptExecuteException(String message) {
        super(message);
    }

    public ScriptExecuteException(String message, Throwable cause) {
        super(message, cause);
    }
}
