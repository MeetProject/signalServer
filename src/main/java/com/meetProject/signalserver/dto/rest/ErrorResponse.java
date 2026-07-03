package com.meetProject.signalserver.dto.rest;

import com.meetProject.signalserver.constant.ErrorCode;

public record ErrorResponse(ErrorCode code, String message) {}
