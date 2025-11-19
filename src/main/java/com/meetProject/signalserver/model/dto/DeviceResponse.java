package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.model.MediaOption;
import com.meetProject.signalserver.util.RandomIdGenerator;

public record DeviceResponse(String userId, MediaOption mediaOption) implements TopicResponse {
    private static final String id = RandomIdGenerator.uuidGenerator();
    private static final TopicType type = TopicType.DEVICE;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TopicType getType() {
        return type;
    }
}
