package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.constant.StreamType;
import com.meetProject.signalserver.model.MediaOption;
import com.meetProject.signalserver.model.User;

public record OfferResponse(String fromUserId, User user, String fromUserSDP, StreamType streamType, Boolean isScreenSender, MediaOption mediaOption) implements SignalResponse {
    private final static SignalType type = SignalType.OFFER;

    @Override
    public SignalType getType() {
        return type;
    }
}

