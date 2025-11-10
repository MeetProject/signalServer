package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;

public record IceResponse (SignalType type, String fromUserId, String fromCandidate) {}
