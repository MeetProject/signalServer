package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.TopicType;

public interface TopicResponse {
    String getId();
    TopicType getTopicType();
}
