package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;
import java.util.List;

public record ScreenResponse(SignalType type, String screenId, List<String> participants ) {}
