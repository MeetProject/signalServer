package com.meetProject.signalserver.orchestration;

import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.model.socket.signal.ErrorResponse;
import com.meetProject.signalserver.service.domain.RoomsService;
import com.meetProject.signalserver.service.socket.application.SignalMessagingService;
import com.meetProject.signalserver.service.domain.UserService;
import java.io.IOException;
import org.springframework.stereotype.Service;

@Service
public class LeaveOrchestrationService {
    private final UserService userService;
    private final SignalMessagingService signalMessagingService;
    private final RoomsService roomsService;

    public LeaveOrchestrationService(UserService userService, RoomsService roomsService, SignalMessagingService signalMessagingService) {
        this.userService = userService;
        this.roomsService = roomsService;
        this.signalMessagingService = signalMessagingService;
    }

    public void leaveUser(String userId) throws IOException {
        try {
            String roomId = userService.getRoomId(userId);
            if(roomId == null) {
                throw new IllegalArgumentException(ErrorMessage.ROOM_NULL);
            }

            leaveUser(userId, roomId);
        } catch(Exception e){
            ErrorResponse response = new ErrorResponse(ErrorCode.E001, e.getMessage());
            signalMessagingService.sendError(userId, response);
        }
    }

    public void forceLeave(String userId, String roomId) {
        try {
            leaveUser(userId, roomId);
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    private void leaveUser(String userId, String roomId) throws IOException {
        roomsService.removeParticipant(roomId, userId);
        userService.updateRoomStatus(userId, null);
        signalMessagingService.sendLeave(userId, roomId);
    }
}
