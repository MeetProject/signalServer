package com.meetProject.signalserver.exception;

import com.meetProject.signalserver.constant.ErrorCode;

public abstract class BusinessException extends RuntimeException {
    private final ErrorCode code;

    protected BusinessException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ErrorCode getCode() {
        return code;
    }
}
