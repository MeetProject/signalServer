package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.model.dto.envelop.SignalEnvelope;
import java.util.List;

public record JoinResponse(String userId, String roomId, List<User> participants) {}
