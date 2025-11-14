package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.constant.StreamType;

public record IceResponse (SignalType type, String fromUserId, String fromUserIce, StreamType streamType) {}
