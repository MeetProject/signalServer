package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.model.MediaOption;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.util.RandomIdGenerator;

public record ParticipantResponse(String userId, User user, MediaOption mediaOption) implements TopicResponse {
    private static final String id = RandomIdGenerator.uuidGenerator();
    private static final TopicType type = TopicType.PARTICIPANT;

    public String getId() {
        return id;
    }

    public TopicType getTopicType() {
        return type;
    }
}
