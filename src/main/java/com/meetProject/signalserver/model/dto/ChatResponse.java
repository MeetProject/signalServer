package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.util.RandomIdGenerator;

public record ChatResponse(String userId, String message, long timestamp) implements TopicResponse{
    private final static TopicType type = TopicType.CHAT;
    private final static String id = RandomIdGenerator.uuidGenerator();

    @Override
    public String getId() {
        return id;
    }
    @Override
    public TopicType getType() {
        return type;
    }
}
