package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.constant.StreamType;
import com.meetProject.signalserver.model.MediaOption;

public record SDPResponse(SignalType type, String fromUserId, String fromUserSDP, StreamType streamType, Boolean isScreenSender, MediaOption mediaOption) implements SignalResponse {
    @Override
    public SignalType getType() {
        return type;
    }
}
