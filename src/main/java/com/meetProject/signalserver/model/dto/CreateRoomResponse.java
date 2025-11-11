package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;

public record CreateRoomResponse(SignalType type, String roomId) {}
