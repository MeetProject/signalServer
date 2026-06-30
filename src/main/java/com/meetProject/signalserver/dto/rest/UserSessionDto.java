package com.meetProject.signalserver.dto.rest;

public class UserSessionDto {
    public record RegisterPayload(String userName, String userColor) {}
    public record RegisterResponse(String userId) {}
}
