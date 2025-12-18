package com.meetProject.signalserver.model.socket.topic;

import com.meetProject.signalserver.util.RandomIdGenerator;

public record ChatResponse(String userId, String message, long timestamp){
    private final static String id = RandomIdGenerator.uuidGenerator();
    public String getId() {
        return id;
    }
}
