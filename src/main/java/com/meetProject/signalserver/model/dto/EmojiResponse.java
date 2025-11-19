package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.Emoji;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.util.RandomIdGenerator;

public record EmojiResponse(String userId, Emoji emoji, long timestamp) implements TopicResponse {
    private static  final String id = RandomIdGenerator.uuidGenerator();
    private static final TopicType type = TopicType.EMOJI;

    @Override
    public String getId() {
        return id;
    }
    @Override
    public TopicType getType() {
        return type;
    }
}
