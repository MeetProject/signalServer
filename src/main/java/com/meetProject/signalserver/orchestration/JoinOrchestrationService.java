package com.meetProject.signalserver.orchestration;

import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.model.dto.common.Participant;
import com.meetProject.signalserver.model.dto.common.User;
import com.meetProject.signalserver.model.dto.socket.ErrorResponse;
import com.meetProject.signalserver.model.dto.socket.RoomSessionDto.JoinPayload;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.UserService;
import com.meetProject.signalserver.service.message.SignalMessagingService;
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

    public void joinRoom(String userId, JoinPayload joinPayload) {
        try {
            User user = userService.getUser(userId);
            if(user == null){
                throw new IllegalArgumentException(ErrorMessage.USER_NOT_FOUND);
            }

            String roomId = joinPayload.roomId();
            userService.updateRoomStatus(userId, roomId);

            Participant participant = new Participant(user, joinPayload.mediaOption(), joinPayload.producers());

            List<Participant> participants = roomsService.getParticipants(roomId).stream().toList();
            roomsService.addParticipant(roomId, participant);

            signalMessagingService.sendJoin(user, joinPayload, participants);
        } catch(Exception e){
            ErrorResponse response = new ErrorResponse(ErrorCode.E001, e.getMessage());
            signalMessagingService.sendError(userId, response);
        }

    }
}
