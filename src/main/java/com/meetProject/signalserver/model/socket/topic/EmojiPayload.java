package com.meetProject.signalserver.model.socket.topic;

import com.meetProject.signalserver.constant.Emoji;

public record EmojiPayload(String roomId, Emoji emoji) {}
