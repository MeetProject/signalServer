package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.constant.StreamType;

public record SDPResponse(SignalType type, String fromUserId, String fromUserSDP, StreamType streamType, Boolean isScreenSender) implements SignalResponse {
    @Override
    public SignalType getType() {
        return type;
    }
}
