package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;

public record RegisterResponse(SignalType type, String userId) {}
