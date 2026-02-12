package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;

public record AnswerResponse(String userId, String sdp, String roomId) implements SignalResponse {
    private final static SignalType type = SignalType.ANSWER;
    @Override
    public SignalType getSignalType() {
        return type;
    }
}
