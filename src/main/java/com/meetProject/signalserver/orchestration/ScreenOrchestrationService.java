package com.meetProject.signalserver.orchestration;

import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.ScreenSharingService;
import com.meetProject.signalserver.service.SignalMessagingService;
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

    public void shareScreen(String userId, String trackId){
            signalMessagingService.shareScreen(userId, trackId);
    }

}
