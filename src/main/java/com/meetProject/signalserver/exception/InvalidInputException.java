package com.meetProject.signalserver.exception;

import com.meetProject.signalserver.constant.ErrorCode;

public class InvalidInputException extends BusinessException {
    public InvalidInputException(String message) {
        super(ErrorCode.INVALID_INPUT, message);
    }
}
