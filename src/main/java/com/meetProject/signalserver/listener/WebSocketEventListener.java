package com.meetProject.signalserver.listener;

import com.meetProject.signalserver.orchestration.LeaveOrchestrationService;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.UserService;
import com.meetProject.signalserver.util.WebSocketUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
    private final RoomsService roomsService;
    private final UserService userService;
    private final LeaveOrchestrationService leaveService;
    public WebSocketEventListener(RoomsService roomsService, UserService userService, LeaveOrchestrationService leaveService) {
        this.roomsService = roomsService;
        this.userService = userService;
        this.leaveService = leaveService;
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        String userId = WebSocketUtils.getUserId(event.getUser());
        if (userId == null) {
            return;
        }

        String roomId = roomsService.getRoomId(userId);
        if(roomId != null) {
            leaveService.forceLeave(userId, roomId);
        }
        userService.removeUser(userId);
    }
}
