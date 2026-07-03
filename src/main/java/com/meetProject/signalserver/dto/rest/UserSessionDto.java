package com.meetProject.signalserver.dto.rest;

import jakarta.validation.constraints.NotBlank;

public class UserSessionDto {
    public record RegisterRequest(
            @NotBlank(message = "사용자 이름은 필수입니다.") String userName,
            @NotBlank(message = "프로필 색상은 필수입니다.") String userColor
    ) {}
    public record RegisterResponse(String userId) {}
}
