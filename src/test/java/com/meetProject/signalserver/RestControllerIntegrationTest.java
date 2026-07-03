package com.meetProject.signalserver;

import static org.assertj.core.api.Assertions.assertThat;

import com.meetProject.signalserver.domain.MediaOption;
import com.meetProject.signalserver.domain.Participant;
import com.meetProject.signalserver.domain.User;
import com.meetProject.signalserver.dto.rest.RoomDto.CreateRoomResponse;
import com.meetProject.signalserver.dto.rest.RoomDto.LeaveRequest;
import com.meetProject.signalserver.dto.rest.RoomDto.ValidateRoomResponse;
import com.meetProject.signalserver.dto.rest.UserSessionDto.RegisterRequest;
import com.meetProject.signalserver.dto.rest.UserSessionDto.RegisterResponse;
import com.meetProject.signalserver.repository.RoomSessionRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class RestControllerIntegrationTest extends IntegrationTestSupport {
    @Autowired
    TestRestTemplate rest;

    @Autowired
    RoomSessionRepository roomSessionRepository;

    private String createRoom() {
        ResponseEntity<CreateRoomResponse> response =
                rest.postForEntity("/api/rooms", null, CreateRoomResponse.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return response.getBody().roomId();
    }

    @Test
    @DisplayName("사용자를 등록하면 201과 userId를 반환한다")
    void registerUser() {
        ResponseEntity<RegisterResponse> response = rest.postForEntity(
                "/api/users", new RegisterRequest("홍길동", "#ffffff"), RegisterResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().userId()).isNotBlank();
    }

    @Test
    @DisplayName("방을 생성하면 201과 roomId를 반환한다")
    void createRoomEndpoint() {
        ResponseEntity<CreateRoomResponse> response =
                rest.postForEntity("/api/rooms", null, CreateRoomResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().roomId()).isNotBlank();
    }

    @Test
    @DisplayName("존재하는 방 검증은 true를 반환한다")
    void validateExistingRoom() {
        String roomId = createRoom();

        ResponseEntity<ValidateRoomResponse> response =
                rest.getForEntity("/api/rooms/" + roomId + "/validate", ValidateRoomResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().value()).isTrue();
    }

    @Test
    @DisplayName("참여 중인 사용자의 퇴장 요청은 200과 함께 세션을 정리한다")
    void leaveRoom() {
        String roomId = createRoom();
        String userId = "u-" + roomId;
        roomSessionRepository.join(roomId, Participant.join(
                User.restore(userId, "홍길동", "#ffffff", LocalDateTime.now()), new MediaOption(true, true)));

        ResponseEntity<Void> response =
                rest.postForEntity("/api/rooms/leave", new LeaveRequest(userId), Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(roomSessionRepository.findRoomIdByUserId(userId)).isEmpty();
    }
}
