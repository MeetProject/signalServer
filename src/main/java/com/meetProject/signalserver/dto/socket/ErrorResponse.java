package com.meetProject.signalserver.dto.socket;

import com.meetProject.signalserver.constant.ErrorCode;

public record ErrorResponse(String correlationId, ErrorCode code, String message) implements SignalResponse {}
