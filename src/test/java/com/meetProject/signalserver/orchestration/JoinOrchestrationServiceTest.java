package com.meetProject.signalserver.orchestration;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.model.Room;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.ScreenSharingService;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.service.UserService;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class JoinOrchestrationServiceTest {

    private UserService userService;
    private RoomsService roomsService;
    private SignalMessagingService signalMessagingService;
    private JoinOrchestrationService joinService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        roomsService = new RoomsService();
        ScreenSharingService screenSharingService = new ScreenSharingService();
        signalMessagingService = mock(SignalMessagingService.class);


        joinService = new JoinOrchestrationService(userService, roomsService, screenSharingService, signalMessagingService);

        // 테스트용 초기 유저와 방 생성
        User user1 = new User("userId", "user", "#000000", null);

        userService.addUser(user1);

        Room room1 = new Room("roomId", new ArrayList<>());
        roomsService.createRoom(room1);
    }

    @Test
    @DisplayName("유저 방 참여")
    void joinUserSuccessfully() {
        joinService.joinRoom("userId", "roomId");

        assertThat(userService.getUser("userId").roomId()).isEqualTo("roomId");
        assertThat(roomsService.getParticipants("roomId")).contains("userId");

        verify(signalMessagingService, times(1)).sendJoin(eq("userId"), any()); // join 시그널 전송 확인
    }

    @Test
    @DisplayName("이미 방에 참여 중인 유저가 다시 참여 시도")
    void joinUserAlreadyInRoom() {
        joinService.joinRoom("userId", "roomId");
        joinService.joinRoom("userId", "roomId");

        verify(signalMessagingService, times(1)).sendError(eq("userId"), argThat(response -> response.message().equals(ErrorMessage.ROOM_ALREADY_JOINED)));
    }

    @Test
    @DisplayName("존재하지 않는 방에 참여 시도")
    void joinUserNonExistentRoom() {
        joinService.joinRoom("userId", "nonExistRoom");

        verify(signalMessagingService, times(1)).sendError(eq("userId"), argThat(response -> response.message().equals(ErrorMessage.ROOM_NOT_FOUND)));
    }
}