package com.meetProject.signalserver.orchestration;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.meetProject.signalserver.model.Room;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.ScreenSharingService;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.service.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class ScreenSharingOrchestrationServiceTest {

    private ScreenSharingService screenSharingService;
    private SignalMessagingService signalMessagingService;
    private ScreenOrchestrationService screenService;

    @BeforeEach
    void setUp() {
        UserService userService = new UserService();
        screenSharingService = new ScreenSharingService();
        RoomsService roomsService = new RoomsService();
        signalMessagingService = mock(SignalMessagingService.class);
        screenService = new ScreenOrchestrationService(roomsService, screenSharingService, signalMessagingService);

        User user1 = new User("user1", "#000000", "user1Id", "roomId");
        User user2 = new User("user2", "#000000", "user2Id", "roomId");

        userService.addUser(user1);
        userService.addUser(user2);

        Room room1 = new Room("roomId", new ArrayList<>(List.of("user1Id", "user2Id")));
        roomsService.createRoom(room1);
    }

    @Test
    @DisplayName("화면 공유 시작 테스트")
    void startScreenSharing() {
        screenService.shareScreen("user1Id", "roomId");
        assertThat(screenSharingService.isSharing("user1Id", "roomId")).isTrue();
    }

    @Test
    @DisplayName("이미 공유 중인 방에서 화면 공유 시도 예외")
    void startScreenSharingAlreadySharing() {
        screenService.shareScreen("user1Id", "roomId");
        screenService.shareScreen("user2Id", "roomId");

        verify(signalMessagingService, times(1)).sendError(eq("user2Id"), argThat(response -> response.message().contains("이미 화면 공유 중")));
    }

}