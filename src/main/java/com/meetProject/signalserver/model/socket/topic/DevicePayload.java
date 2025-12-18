package com.meetProject.signalserver.model.socket.topic;

import com.meetProject.signalserver.model.MediaOption;

public record DevicePayload ( String roomId, MediaOption mediaOption) {}
