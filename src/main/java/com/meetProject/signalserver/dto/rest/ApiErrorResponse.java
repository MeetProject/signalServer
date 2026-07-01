package com.meetProject.signalserver.dto.rest;

import com.meetProject.signalserver.constant.ErrorCode;

public record ApiErrorResponse(ErrorCode code, String message) {}
