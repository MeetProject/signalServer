package com.meetProject.signalserver.exception;

import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.constant.ErrorMessage;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND, ErrorMessage.USER_NOT_FOUND);
    }
}
