package com.meetProject.signalserver.dto.socket;

import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.dto.common.SignalResponse;

public record ErrorResponse(ErrorCode code, String message) implements SignalResponse {
    @Override
    public String correlationId() {
        return null;
    }
}
