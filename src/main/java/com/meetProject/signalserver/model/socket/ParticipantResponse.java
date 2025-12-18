package com.meetProject.signalserver.model.socket;

import com.meetProject.signalserver.model.MediaOption;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.util.RandomIdGenerator;

public record ParticipantResponse(String userId, String roomId, User user, MediaOption mediaOption)  {
    private static final String id = RandomIdGenerator.uuidGenerator();

    public String getId() {
        return id;
    }

}
