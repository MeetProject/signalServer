package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;

public record IcePayload(SignalType type, String fromUserId, String toUserId, String fromCandidate) {}
