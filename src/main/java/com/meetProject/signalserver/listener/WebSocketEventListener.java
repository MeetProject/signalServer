package com.meetProject.signalserver.listener;

import com.meetProject.signalserver.constant.StreamType;
import com.meetProject.signalserver.service.RoomsManagementService;
import com.meetProject.signalserver.service.ScreenSharingService;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.service.UserManagementService;
import com.meetProject.signalserver.util.WebSocketUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
    private final UserManagementService userManagementService;
    private final RoomsManagementService roomsManagementService;
    private final ScreenSharingService screenSharingService;
    private final SignalMessagingService signalMessagingService;

    public WebSocketEventListener(UserManagementService userManagementService,
                                  RoomsManagementService roomsManagementService,
                                  ScreenSharingService screenSharingService,
                                  SignalMessagingService signalMessagingService) {
        this.userManagementService = userManagementService;
        this.roomsManagementService = roomsManagementService;
        this.screenSharingService = screenSharingService;
        this.signalMessagingService = signalMessagingService;
    }

    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        String userId = WebSocketUtils.getUserId(event.getUser());
        if (userId == null) {
            return;
        }

        String roomId = userManagementService.getUserRoomStatus(userId);
        userManagementService.removeUser(userId);
        if (roomId == null) {
            return;
        }

        if (screenSharingService.isScreenSharingId(userId)) {
            screenSharingService.stopSharing(userId);
            signalMessagingService.sendLeave(roomId, userId, StreamType.SCREEN);
        }

        signalMessagingService.sendLeave(roomId, userId, StreamType.USER);
        roomsManagementService.removeParticipant(roomId, userId);
    }
}
