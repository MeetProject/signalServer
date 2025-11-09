package com.meetProject.signalserver.model;

public class JoinPayload {
    private final String userId;
    private final String roomId;

    // Jackson이 필요로 하는 기본 생성자
    public JoinPayload(String userId, String roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }

    public String getUserId() {
        return userId;
    }
    public String getRoomId() {
        return roomId;
    }
}