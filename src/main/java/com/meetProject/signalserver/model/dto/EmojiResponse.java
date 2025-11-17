package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.Emoji;
import com.meetProject.signalserver.constant.TopicType;

public record EmojiResponse(String userId, Emoji emoji) implements TopicResponse {
    private static final TopicType type = TopicType.EMOJI;

    @Override
    public TopicType getType() {
        return type;
    }
}
