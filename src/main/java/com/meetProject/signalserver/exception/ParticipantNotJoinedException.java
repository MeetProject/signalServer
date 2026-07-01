package com.meetProject.signalserver.exception;

import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.constant.ErrorMessage;

public class ParticipantNotJoinedException extends BusinessException {
    public ParticipantNotJoinedException() {
        super(ErrorCode.USER_NOT_JOINED, ErrorMessage.USER_NOT_JOINED);
    }
}
