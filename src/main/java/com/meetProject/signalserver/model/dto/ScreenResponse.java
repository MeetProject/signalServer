package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.model.User;
import java.util.List;

public record ScreenResponse(SignalType type, String screenId, List<User> participants ) {}
