package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.model.MediaOption;

public record DeviceResponse( String userId, MediaOption mediaOption) implements TopicResponse {
    private static final TopicType type = TopicType.DEVICE;

    @Override
    public TopicType getType() {
        return type;
    }
}
