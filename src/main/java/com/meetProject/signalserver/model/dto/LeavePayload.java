package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.StreamType;

public record LeavePayload(String roomId, StreamType streamType) {}
