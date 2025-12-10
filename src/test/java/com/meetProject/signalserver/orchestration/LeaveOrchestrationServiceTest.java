package com.meetProject.signalserver.orchestration;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.meetProject.signalserver.constant.StreamType;
import com.meetProject.signalserver.model.Room;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.ScreenSharingService;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.service.UserService;
import com.meetProject.signalserver.service.WebSocketUserService;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class LeaveOrchestrationServiceTest {
    private UserService userService;
    private ScreenSharingService screenSharingService;
    private LeaveOrchestrationService leaveService;

    @BeforeEach
    void setUp() {
        SimpMessagingTemplate simpMessagingTemplate = mock(SimpMessagingTemplate.class);

        userService = new UserService();
        RoomsService roomService = new RoomsService();
        screenSharingService = new ScreenSharingService();
        WebSocketUserService webSocketUserService = new WebSocketUserService();
        SignalMessagingService signalMessagingService = new SignalMessagingService(simpMessagingTemplate, webSocketUserService);
        leaveService = new LeaveOrchestrationService(userService, roomService, screenSharingService,
                signalMessagingService);

        User user = new User("user1Id", "#000000", "user1", "room1", false);
        userService.addUser(user);
        Room room = new Room("room1", new ArrayList<>());
        room.addParticipant("user1");
        roomService.createRoom(room);
    }

    @Test
    @DisplayName("화면 공유 안한 사용자 나가기 테스트")
    void leaveUserWithNoneScreenShare() {
        leaveService.leaveUser("user1Id", StreamType.USER);
        assertThat(userService.getUser("user1Id").roomId()).isNull();
    }

    @Test
    @DisplayName("화면 공유 사용자 나가기 테스트")
    void leaveUserWithScreenShare() {
        screenSharingService.startSharing("room1", "user1Id");
        leaveService.leaveUser("user1Id", StreamType.USER);

        assertThat(userService.getUser("user1Id").roomId()).isNull();
        assertThat(screenSharingService.isSharing("room1", "user1Id")).isEqualTo(false);
    }

}
