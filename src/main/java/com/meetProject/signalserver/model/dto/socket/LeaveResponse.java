package com.meetProject.signalserver.model.dto.socket;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.model.dto.common.SignalResponse;
import com.meetProject.signalserver.model.dto.common.TopicResponse;
import com.meetProject.signalserver.util.RandomIdGenerator;

public record LeaveResponse (String userId) implements TopicResponse, SignalResponse {
    private static final String id = RandomIdGenerator.uuidGenerator();
    private static final TopicType topicType = TopicType.LEAVE;
    private static final SignalType signalType = SignalType.LEAVE;

    @Override
    public String getId() {
        return id;
    }
    @Override
    public TopicType getTopicType() {
        return topicType;
    }

    @Override
    public SignalType getSignalType() {
        return signalType;
    }
}
