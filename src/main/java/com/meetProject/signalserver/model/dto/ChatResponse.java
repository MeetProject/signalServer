package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.util.RandomIdGenerator;

public record ChatResponse(String userId, String message, long timestamp){
    private final static String id = RandomIdGenerator.uuidGenerator();
    public String getId() {
        return id;
    }
}
