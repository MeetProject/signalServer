package com.meetProject.signalserver.controller;

import com.meetProject.signalserver.constant.StreamType;
import com.meetProject.signalserver.service.RoomsManagementService;
import com.meetProject.signalserver.service.ScreenSharingService;
import com.meetProject.signalserver.service.SignalMessagingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ForceLeaveController {
    private final RoomsManagementService roomsManagementService;
    private final ScreenSharingService screenSharingService;
    private final SignalMessagingService signalMessagingService;

    public ForceLeaveController(RoomsManagementService roomsManagementService, ScreenSharingService screenSharingService, SignalMessagingService signalMessagingService) {
        this.roomsManagementService = roomsManagementService;
        this.screenSharingService = screenSharingService;
        this.signalMessagingService = signalMessagingService;
    }

    @PostMapping("/api/leave")
    public void forceLeave(@RequestParam String userId, @RequestParam String roomId) {
        System.out.println("forceLeave");

        if(screenSharingService.isScreenSharingId(userId)) {
            screenSharingService.stopSharing(roomId);
            signalMessagingService.sendLeave(roomId, userId, StreamType.SCREEN);
        }
        roomsManagementService.removeParticipant(roomId, userId);
        signalMessagingService.sendLeave(roomId, userId, StreamType.USER);
    }
}
