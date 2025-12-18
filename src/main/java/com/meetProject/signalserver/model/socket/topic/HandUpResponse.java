package com.meetProject.signalserver.model.socket.topic;

import com.meetProject.signalserver.util.RandomIdGenerator;

public record HandUpResponse (String userId, boolean value) {
    private static final String id = RandomIdGenerator.uuidGenerator();

    public String getId() {
        return id;
    }
}
