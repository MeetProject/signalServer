package com.meetProject.signalserver.orchestration;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.model.dto.ScreenResponse;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.ScreenSharingService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ScreenOrchestrationService {
    private final ScreenSharingService screenSharingService;
    private final RoomsService roomsService;

    public ScreenOrchestrationService(RoomsService roomsService, ScreenSharingService screenSharingService) {
        this.roomsService = roomsService;
        this.screenSharingService = screenSharingService;
    }

    public ScreenResponse shareScreen(String roomId, String userId){
        List<String> participants = roomsService.getParticipants(roomId).stream()
                .filter((participant) -> !participant.equals(userId))
                .toList();
        screenSharingService.startSharing(roomId, userId);
        return new ScreenResponse(SignalType.SCREEN, userId, participants);

    }

}
