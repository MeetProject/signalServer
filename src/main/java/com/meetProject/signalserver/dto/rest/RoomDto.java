package com.meetProject.signalserver.dto.rest;

import jakarta.validation.constraints.NotBlank;

public class RoomDto {
    public record CreateRoomResponse(String roomId)  {}

    public record ValidateRoomResponse(boolean value) {}

    public record LeaveRequest(@NotBlank(message = "사용자 ID는 필수입니다.") String userId) {}
}
