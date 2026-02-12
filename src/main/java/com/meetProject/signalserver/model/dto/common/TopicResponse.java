package com.meetProject.signalserver.model.dto.common;

import com.meetProject.signalserver.constant.TopicType;

public interface TopicResponse {
    String getId();
    TopicType getTopicType();
}
