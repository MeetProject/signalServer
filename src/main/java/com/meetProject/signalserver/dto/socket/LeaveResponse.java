package com.meetProject.signalserver.dto.socket;

import com.meetProject.signalserver.dto.common.SignalResponse;
import com.meetProject.signalserver.dto.common.TopicResponse;

public record LeaveResponse (String userId, String correlationId) implements TopicResponse, SignalResponse {
    @Override
    public String id() {
        return null;
    }
}
