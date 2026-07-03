package com.meetProject.signalserver.exception;

import com.meetProject.signalserver.constant.ErrorCode;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND, "존재하지 않는 사용자입니다.");
    }
}
