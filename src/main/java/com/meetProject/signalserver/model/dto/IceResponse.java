package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.constant.StreamType;

public record IceResponse (String userId, String ice) implements SignalResponse {
    private final static SignalType TYPE = SignalType.ICE;

    @Override
    public SignalType getType() {
        return TYPE;
    }
}
