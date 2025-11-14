package com.meetProject.signalserver.orchestration;

import com.meetProject.signalserver.constant.StreamType;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.ScreenSharingService;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class LeaveOrchestrationService {
    private final UserService userService;
    private final ScreenSharingService screenSharingService;
    private final SignalMessagingService signalMessagingService;
    private final RoomsService roomsService;

    public LeaveOrchestrationService(UserService userService, RoomsService roomsService, ScreenSharingService screenSharingService, SignalMessagingService signalMessagingService) {
        this.userService = userService;
        this.roomsService = roomsService;
        this.screenSharingService = screenSharingService;
        this.signalMessagingService = signalMessagingService;
    }

    public void leaveUser(String userId, StreamType streamType) {
        String roomId = userService.getRoomId(userId);
        if(roomId == null) {
            throw new IllegalArgumentException("Room Id is null");
        }

        if(screenSharingService.isSharing(userId)) {
            screenSharingService.stopSharing(roomId);
            signalMessagingService.sendLeave(roomId, userId, StreamType.SCREEN);
        }

        if(streamType.equals(StreamType.USER)) {
            roomsService.removeParticipant(roomId, userId);
            userService.updateRoomStatus(userId, null);
            signalMessagingService.sendLeave(roomId, userId, StreamType.USER);
        }
    }
}
