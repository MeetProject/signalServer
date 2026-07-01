package com.meetProject.signalserver.exception;

import com.meetProject.signalserver.constant.ErrorCode;

public class ParticipantNotJoinedException extends BusinessException {
    public ParticipantNotJoinedException() {
        super(ErrorCode.USER_NOT_JOINED, "참여 중인 방이 없습니다.");
    }
}
