package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.StreamType;
import com.meetProject.signalserver.constant.TopicType;

public record LeaveResponse (String fromUserId, StreamType streamType) implements TopicResponse{
    private final static  TopicType type = TopicType.LEAVE;
    @Override
    public TopicType getType() {
        return type;
    }
}
