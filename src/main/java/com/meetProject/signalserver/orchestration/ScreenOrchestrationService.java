package com.meetProject.signalserver.orchestration;

import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.model.dto.ErrorResponse;
import com.meetProject.signalserver.model.dto.ScreenResponse;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.ScreenSharingService;
import com.meetProject.signalserver.service.SignalMessagingService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ScreenOrchestrationService {
    private final ScreenSharingService screenSharingService;
    private final RoomsService roomsService;
    private final SignalMessagingService signalMessagingService;

    public ScreenOrchestrationService(RoomsService roomsService, ScreenSharingService screenSharingService, SignalMessagingService signalMessagingService) {
        this.roomsService = roomsService;
        this.screenSharingService = screenSharingService;
        this.signalMessagingService = signalMessagingService;
    }

    public void shareScreen(String userId, String roomId){
        try {
            List<String> participants = roomsService.getParticipants(roomId).stream()
                    .filter((participant) -> !participant.equals(userId))
                    .toList();
            screenSharingService.startSharing(roomId, userId);
            ScreenResponse response = new ScreenResponse(userId, participants);
            signalMessagingService.shareScreen(userId, response);
        } catch(Exception e){
            ErrorResponse response = new ErrorResponse(ErrorCode.E001, e.getMessage());
            signalMessagingService.sendError(userId, response);
        }

    }

}
