package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.constant.SignalType;

public record ErrorResponse(ErrorCode code, String message){}
