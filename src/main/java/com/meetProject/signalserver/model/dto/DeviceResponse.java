package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.model.MediaOption;
import com.meetProject.signalserver.util.RandomIdGenerator;

public record DeviceResponse(String userId, MediaOption mediaOption) {
    private static final String id = RandomIdGenerator.uuidGenerator();

    public String getId() {
        return id;
    }
}
