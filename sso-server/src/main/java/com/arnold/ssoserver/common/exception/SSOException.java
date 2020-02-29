package com.arnold.ssoserver.common.exception;

public class SSOException extends RuntimeException {


    public SSOException() {
    }

    public SSOException(String msg) {
        super(msg);
    }

    public SSOException(Throwable cause) {
        super(cause);
    }

    public SSOException(String message, Throwable cause) {
        super(message, cause);
    }

    public SSOException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
