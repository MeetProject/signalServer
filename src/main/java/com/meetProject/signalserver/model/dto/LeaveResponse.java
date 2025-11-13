package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.constant.StreamType;

public record LeaveResponse (SignalType type, String fromUserId, StreamType streamType) {}
