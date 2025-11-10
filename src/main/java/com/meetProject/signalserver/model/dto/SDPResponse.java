package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;

public record SDPResponse(SignalType type, String fromUserId, String fromUserSDP) {}
