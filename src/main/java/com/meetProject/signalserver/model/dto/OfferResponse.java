package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;

public record OfferResponse(String userId, String roomId, String sdp) {}

