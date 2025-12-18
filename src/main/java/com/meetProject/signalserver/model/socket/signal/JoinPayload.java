package com.meetProject.signalserver.model.socket.signal;

import com.meetProject.signalserver.model.MediaOption;

public record JoinPayload(String roomId, MediaOption mediaOption) {}