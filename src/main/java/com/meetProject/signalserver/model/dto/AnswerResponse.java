package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.constant.StreamType;
import com.meetProject.signalserver.model.MediaOption;

public record AnswerResponse(String fromUserId, String fromUserSDP, StreamType streamType, Boolean isScreenSender, MediaOption mediaOption) implements SignalResponse {
    private final static SignalType type = SignalType.ANSWER;
    @Override
    public SignalType getType() {
        return type;
    }
}
