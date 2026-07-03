package com.meetProject.signalserver.dto.rest;

public class RoomDto {
    public record CreateRoomResponse(String roomId)  {}

    public record ValidateRoomResponse(boolean value) {}

    public record LeaveRequest(String userId) {}
}
