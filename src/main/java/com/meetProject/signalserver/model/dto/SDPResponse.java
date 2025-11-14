package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.constant.StreamType;

public record SDPResponse(SignalType type, String fromUserId, String fromUserSDP, StreamType streamType, Boolean isScreenSender) {}
