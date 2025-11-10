package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;

public record LeaveResponse (SignalType type, String fromUserId) {}
