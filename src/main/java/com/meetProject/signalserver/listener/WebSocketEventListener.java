package com.meetProject.signalserver.listener;

import com.meetProject.signalserver.service.RoomsManagementService;
import com.meetProject.signalserver.service.UserManagementService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
public class WebSocketEventListener {
    private final UserManagementService userManagementService;
    private final RoomsManagementService roomsManagementService;

    public WebSocketEventListener(UserManagementService userManagementService, RoomsManagementService roomsManagementService) {
        this.userManagementService = userManagementService;
        this.roomsManagementService = roomsManagementService;
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionConnectedEvent event) {
        String userId = event.getUser().getName();

        if (userId != null) {
            roomsManagementService.removeParticipantFromAllRooms(userId);
            userManagementService.removeUser(userId);
        }
    }
}
