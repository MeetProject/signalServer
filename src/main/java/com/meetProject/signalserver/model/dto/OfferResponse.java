package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;

public record OfferResponse(String userId, String roomId, String sdp) implements SignalResponse {
    private final static SignalType type = SignalType.OFFER;

    @Override
    public SignalType getType() {
        return type;
    }
}

