package com.meetProject.signalserver.model.socket;

import com.meetProject.signalserver.util.RandomIdGenerator;

public record LeaveResponse (String userId)  {
    private static final String id = RandomIdGenerator.uuidGenerator();

    public String getId() {
        return id;
    }
}
