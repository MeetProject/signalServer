package com.meetProject.signalserver.orchestration;

import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.model.MediaOption;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.model.socket.signal.ErrorResponse;
import com.meetProject.signalserver.service.domain.RoomsService;
import com.meetProject.signalserver.service.socket.application.SignalMessagingService;
import com.meetProject.signalserver.service.domain.UserService;
import java.io.IOException;
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

    public void joinRoom(String userId, String roomId, MediaOption mediaOption) throws IOException {
        try {
            User user = validateUser(userId);
            userService.updateRoomStatus(userId, roomId);

            List<User> participants = roomsService.getParticipants(roomId).stream()
                    .map(userService::getUser)
                    .toList();

            roomsService.addParticipant(roomId, userId);

            signalMessagingService.sendJoin(userId, roomId, participants);
            signalMessagingService.sendParticipant(userId, roomId, user, mediaOption );

        } catch(Exception e){
            ErrorResponse response = new ErrorResponse(ErrorCode.E001, e.getMessage());
            signalMessagingService.sendError(userId, response);
        }

    }

    private User validateUser(String userId) {
        User user = userService.getUser(userId);
        if (user == null) {
            throw new IllegalArgumentException(ErrorMessage.USER_NOT_FOUND);
        }
        return user;
    }
}
