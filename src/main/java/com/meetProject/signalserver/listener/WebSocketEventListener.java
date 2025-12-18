package com.meetProject.signalserver.listener;

import com.meetProject.signalserver.orchestration.LeaveOrchestrationService;
import com.meetProject.signalserver.service.domain.UserService;
import com.meetProject.signalserver.util.WebSocketUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
    private final UserService userService;
    private final LeaveOrchestrationService leaveService;
    public WebSocketEventListener(UserService userService, LeaveOrchestrationService leaveService) {
        this.userService = userService;
        this.leaveService = leaveService;
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        String userId = WebSocketUtils.getUserId(event.getUser());
        if (userId == null) {
            return;
        }

        String roomId = userService.getRoomId(userId);
        userService.removeUser(userId);
        if (roomId == null) {
            return;
        }

        leaveService.forceLeave(userId, roomId);
    }
}
