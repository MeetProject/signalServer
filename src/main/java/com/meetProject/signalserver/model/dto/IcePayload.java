package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.StreamType;

public record IcePayload(String toUserId, String fromCandidate, StreamType streamType) {}
