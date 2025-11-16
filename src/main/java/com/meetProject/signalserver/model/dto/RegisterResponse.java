package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;

public record RegisterResponse(SignalType type, String userId) implements SignalResponse {
    private final static SignalType TYPE = SignalType.REGISTER;

    @Override
    public SignalType getType() {
        return TYPE;
    }
}
