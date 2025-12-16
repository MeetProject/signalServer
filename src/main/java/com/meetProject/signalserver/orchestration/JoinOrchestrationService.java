package com.meetProject.signalserver.orchestration;

import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.model.MediaOption;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.model.dto.ErrorResponse;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.service.UserService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class JoinOrchestrationService {
    private final UserService userService;
    private final RoomsService roomsService;
    private final SignalMessagingService signalMessagingService;

    public JoinOrchestrationService(UserService userService, RoomsService roomService, SignalMessagingService signalMessagingService) {
        this.userService = userService;
        this.roomsService = roomService;
        this.signalMessagingService = signalMessagingService;
    }

    public void joinRoom(String userId, String roomId, MediaOption mediaOption) {
        try {
            User user = userService.getUser(userId);
            if(user == null){
                throw new IllegalArgumentException(ErrorMessage.USER_NOT_FOUND);
            }

            System.out.println(roomId + ": " + userId);

            userService.updateRoomStatus(userId, roomId);

            List<User> participants = roomsService.getParticipants(roomId).stream()
                    .map(userService::getUser)
                    .toList();

            roomsService.addParticipant(roomId, userId);
            signalMessagingService.sendJoin(userId, roomId, user, participants, mediaOption);

        } catch(Exception e){
            ErrorResponse response = new ErrorResponse(ErrorCode.E001, e.getMessage());
            signalMessagingService.sendError(userId, response);
        }

    }
}
