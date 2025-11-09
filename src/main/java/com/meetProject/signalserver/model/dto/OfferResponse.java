package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;

public record OfferResponse(SignalType type, String fromUserId, String fromUserSDP) {}
