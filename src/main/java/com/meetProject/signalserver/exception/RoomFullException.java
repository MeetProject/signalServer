package com.meetProject.signalserver.exception;

import com.meetProject.signalserver.constant.ErrorCode;

public class RoomFullException extends BusinessException {
    public RoomFullException() {
        super(ErrorCode.ROOM_FULL, "방 인원이 가득 찼습니다.");
    }
}
