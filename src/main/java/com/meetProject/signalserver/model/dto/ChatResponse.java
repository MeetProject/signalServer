package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.TopicType;

public record ChatResponse(String id, String userId, String message, long timestamp) implements TopicResponse{
    private final static TopicType type = TopicType.CHAT;

    @Override
    public TopicType getType() {
        return type;
    }
}
