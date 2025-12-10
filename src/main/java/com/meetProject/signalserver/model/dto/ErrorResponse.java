package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.constant.SignalType;

public record ErrorResponse(ErrorCode code, String message) implements SignalResponse{
    private final static SignalType TYPE = SignalType.ERROR;

    @Override
    public SignalType getSignalType() {
        return TYPE;
    }
}
