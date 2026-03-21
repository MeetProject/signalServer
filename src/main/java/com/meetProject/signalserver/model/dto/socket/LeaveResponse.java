package com.meetProject.signalserver.model.dto.socket;

import com.meetProject.signalserver.model.dto.common.SignalResponse;
import com.meetProject.signalserver.model.dto.common.TopicResponse;

public record LeaveResponse (String userId, String correlationId) implements TopicResponse, SignalResponse {
    @Override
    public String id() {
        return null;
    }
}
