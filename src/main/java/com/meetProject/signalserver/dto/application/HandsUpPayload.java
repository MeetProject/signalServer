package com.meetProject.signalserver.dto.application;

public record HandsUpPayload(String roomId, String userId, boolean isHandsUp) {
}
