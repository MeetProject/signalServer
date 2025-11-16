package com.meetProject.signalserver.listener;

import static org.mockito.Mockito.*;

import com.meetProject.signalserver.orchestration.LeaveOrchestrationService;
import com.meetProject.signalserver.service.UserService;
import com.meetProject.signalserver.util.WebSocketUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.MockedStatic;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

public class WebSocketEventListenerTest {

    private UserService userService;
    private LeaveOrchestrationService leaveService;
    private WebSocketEventListener listener;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        leaveService = mock(LeaveOrchestrationService.class);
        listener = new WebSocketEventListener(userService, leaveService);
    }

    @Test
    @DisplayName("userId가 null인 경우")
    void handleDisconnect_UserIdNull() {
        SessionDisconnectEvent event = mock(SessionDisconnectEvent.class);

        try (MockedStatic<WebSocketUtils> utilities = mockStatic(WebSocketUtils.class)) {
            utilities.when(() -> WebSocketUtils.getUserId(event.getUser())).thenReturn(null);
            listener.handleWebSocketDisconnect(event);

            verifyNoInteractions(userService, leaveService);
        }
    }

    @Test
    @DisplayName("roomId가 null인 경우")
    void handleDisconnect_RoomIdNull() {
        SessionDisconnectEvent event = mock(SessionDisconnectEvent.class);

        try (MockedStatic<WebSocketUtils> utilities = mockStatic(WebSocketUtils.class)) {
            utilities.when(() -> WebSocketUtils.getUserId(event.getUser())).thenReturn("user1");
            when(userService.getRoomId("user1")).thenReturn(null);

            listener.handleWebSocketDisconnect(event);

            verify(userService, times(1)).removeUser("user1");
            verifyNoInteractions(leaveService);
        }
    }

    @Test
    @DisplayName("removeUser와 forceLeave 호출")
    void handleDisconnect_RoomIdExists() {
        SessionDisconnectEvent event = mock(SessionDisconnectEvent.class);

        try (MockedStatic<WebSocketUtils> utilities = mockStatic(WebSocketUtils.class)) {
            utilities.when(() -> WebSocketUtils.getUserId(event.getUser())).thenReturn("user1");
            when(userService.getRoomId("user1")).thenReturn("room1");

            listener.handleWebSocketDisconnect(event);

            verify(userService, times(1)).removeUser("user1");
            verify(leaveService, times(1)).forceLeave("user1", "room1");
        }
    }
}