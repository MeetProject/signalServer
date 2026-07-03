package com.meetProject.signalserver.dto.application;

import com.meetProject.signalserver.domain.MediaOption;

public record DeviceResult(String roomId, String userId, MediaOption mediaOption) {}
