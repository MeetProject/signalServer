package com.meetProject.signalserver.exception;

import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.constant.ErrorMessage;

public class RoomFullException extends BusinessException {
    public RoomFullException() {
        super(ErrorCode.ROOM_FULL, ErrorMessage.ROOM_FULL);
    }
}
