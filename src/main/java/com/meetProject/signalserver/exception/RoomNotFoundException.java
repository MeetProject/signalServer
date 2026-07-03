package com.meetProject.signalserver.exception;

import com.meetProject.signalserver.constant.ErrorCode;

public class RoomNotFoundException extends BusinessException {
    public RoomNotFoundException() {
        super(ErrorCode.ROOM_NOT_FOUND, "존재하지 않는 방 입니다.");
    }
}
