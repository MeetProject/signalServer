package com.meetProject.signalserver.listener;

import com.meetProject.signalserver.constant.StreamType;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.ScreenSharingService;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.service.UserService;
import com.meetProject.signalserver.util.WebSocketUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {
    private final UserService userService;
    private final RoomsService roomsService;
    private final ScreenSharingService screenSharingService;
    private final SignalMessagingService signalMessagingService;

    public WebSocketEventListener(UserService userService,
                                  RoomsService roomsService,
                                  ScreenSharingService screenSharingService,
                                  SignalMessagingService signalMessagingService) {
        this.userService = userService;
        this.roomsService = roomsService;
        this.screenSharingService = screenSharingService;
        this.signalMessagingService = signalMessagingService;
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

        if (screenSharingService.isSharing(userId)) {
            screenSharingService.stopSharing(userId);
            signalMessagingService.sendLeave(roomId, userId, StreamType.SCREEN);
        }

        signalMessagingService.sendLeave(roomId, userId, StreamType.USER);
        roomsService.removeParticipant(roomId, userId);
    }
}
