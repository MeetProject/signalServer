package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;

public record CreateRoomResponse(String roomId) implements SignalResponse {
    private final static SignalType TYPE = SignalType.CREATE;

    @Override
    public SignalType getType() {
        return TYPE;
    }
}
