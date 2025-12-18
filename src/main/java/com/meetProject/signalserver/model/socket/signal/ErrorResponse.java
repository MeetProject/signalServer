package com.meetProject.signalserver.model.socket.signal;

import com.meetProject.signalserver.constant.ErrorCode;

public record ErrorResponse(ErrorCode code, String message){}
