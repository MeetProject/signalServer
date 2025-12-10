package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.util.RandomIdGenerator;

public record HandUpResponse (String userId, boolean value) implements TopicResponse {
    private static final String id = RandomIdGenerator.uuidGenerator();
    private static final TopicType type = TopicType.HANDUP;

    @Override
    public String getId() {
        return id;
    }
    @Override
    public TopicType getTopicType() {
        return type;
    }
}
