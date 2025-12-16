package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.util.RandomIdGenerator;

public record HandUpResponse (String userId, boolean value) {
    private static final String id = RandomIdGenerator.uuidGenerator();

    public String getId() {
        return id;
    }
}
