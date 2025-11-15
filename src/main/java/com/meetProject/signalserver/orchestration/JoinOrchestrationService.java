package com.meetProject.signalserver.orchestration;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.model.dto.JoinResponse;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.ScreenSharingService;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.service.UserService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class JoinOrchestrationService {
    private final UserService userService;
    private final RoomsService roomsService;
    private final ScreenSharingService screenSharingService;
    private final SignalMessagingService signalMessagingService;

    public JoinOrchestrationService(UserService userService, RoomsService roomService, ScreenSharingService screenSharingService, SignalMessagingService signalMessagingService) {
        this.userService = userService;
        this.roomsService = roomService;
        this.screenSharingService = screenSharingService;
        this.signalMessagingService = signalMessagingService;
    }

    public void joinRoom(String userId, String roomId) {
        try {
            if(userService.getUser(userId) == null){
                throw new IllegalArgumentException("User not found");
            }

            userService.updateRoomStatus(userId, roomId);

            String screenOwnerId = screenSharingService.getOwnerId(roomId);
            List<User> participants = roomsService.getParticipants(roomId).stream()
                    .map(userService::getUser)
                    .toList();

            roomsService.addParticipant(roomId, userId);
            JoinResponse response = new JoinResponse(SignalType.JOIN, roomId, participants, screenOwnerId);
            signalMessagingService.sendJoin(userId, response);

        } catch(Exception e){
            signalMessagingService.sendError(userId, e.getMessage());
        }

    }
}
