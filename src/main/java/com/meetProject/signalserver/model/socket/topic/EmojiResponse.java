package com.meetProject.signalserver.model.socket.topic;

import com.meetProject.signalserver.constant.Emoji;
import com.meetProject.signalserver.util.RandomIdGenerator;

public record EmojiResponse(String userId, Emoji emoji, long timestamp) {
    private static  final String id = RandomIdGenerator.uuidGenerator();

    public String getId() {
        return id;
    }
}
