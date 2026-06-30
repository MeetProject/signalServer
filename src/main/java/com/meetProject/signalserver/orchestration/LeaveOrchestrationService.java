package com.meetProject.signalserver.orchestration;

import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.dto.socket.ErrorResponse;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.message.SignalMessagingService;
import com.meetProject.signalserver.service.message.TopicMessagingService;
import org.springframework.stereotype.Service;

@Service
public class LeaveOrchestrationService {
    private final SignalMessagingService signalMessagingService;
    private final TopicMessagingService topicMessagingService;
    private final RoomsService roomsService;

    public LeaveOrchestrationService(RoomsService roomsService, SignalMessagingService signalMessagingService, TopicMessagingService topicMessagingService) {
        this.roomsService = roomsService;
        this.signalMessagingService = signalMessagingService;
        this.topicMessagingService = topicMessagingService;
    }

    public void leaveUser(String userId) {
        try {
            String roomId = roomsService.getRoomId(userId);
            if(roomId == null) {
                throw new IllegalArgumentException(ErrorMessage.ROOM_NULL);
            }

            roomsService.removeParticipant(roomId, userId);
            topicMessagingService.sendLeave(roomId, userId);
        } catch(Exception e){
            ErrorResponse response = new ErrorResponse(ErrorCode.E001, e.getMessage());
            signalMessagingService.sendError(userId, response);
        }
    }

}
