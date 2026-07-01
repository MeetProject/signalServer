package com.meetProject.signalserver.exception;

import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.constant.ErrorMessage;

public class RoomNotFoundException extends BusinessException {
    public RoomNotFoundException() {
        super(ErrorCode.ROOM_NOT_FOUND, ErrorMessage.ROOM_NOT_FOUND);
    }
}
