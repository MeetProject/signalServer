package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;

public record ParticipantPayload(SignalType type, String userId, String roomId) {}