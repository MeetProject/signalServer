package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.StreamType;

public record SDPPayload(String toUserId, String fromUserSDP, StreamType streamType) {}
