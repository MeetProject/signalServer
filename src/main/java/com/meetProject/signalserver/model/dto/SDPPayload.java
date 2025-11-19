package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.StreamType;
import com.meetProject.signalserver.model.MediaOption;

public record SDPPayload(String toUserId, String fromUserSDP, StreamType streamType, MediaOption mediaOption) {}
