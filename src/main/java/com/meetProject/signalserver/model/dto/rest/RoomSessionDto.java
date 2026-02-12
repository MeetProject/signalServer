package com.meetProject.signalserver.model.dto.rest;

public class RoomSessionDto {
    public record CreateRoomResponse(String roomId)  {}

    public record ValidateRoomResponse(boolean value) {}
}
