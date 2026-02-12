package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.Emoji;

public record EmojiPayload(String roomId, Emoji emoji) {}
