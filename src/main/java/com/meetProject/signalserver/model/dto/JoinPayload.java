package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.model.MediaOption;

public record JoinPayload(String roomId, MediaOption mediaOption) {}