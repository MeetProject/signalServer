package com.meetProject.signalserver.controller;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.constant.StreamType;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.model.dto.LeaveResponse;
import com.meetProject.signalserver.service.RoomsManagementService;
import com.meetProject.signalserver.service.ScreenSharingService;
import com.meetProject.signalserver.service.UserManagementService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ForceLeaveController {
    private final RoomsManagementService roomsManagementService;
    private final UserManagementService userManagementService;
    private final ScreenSharingService screenSharingService;
    private final SimpMessagingTemplate messagingTemplate;

    public ForceLeaveController(RoomsManagementService roomsManagementService, UserManagementService userManagementService, ScreenSharingService screenSharingService, SimpMessagingTemplate simpMessagingTemplate) {
        this.roomsManagementService = roomsManagementService;
        this.userManagementService = userManagementService;
        this.screenSharingService = screenSharingService;
        this.messagingTemplate = simpMessagingTemplate;
    }

    @PostMapping("/api/leave")
    public void forceLeave(@RequestParam String userId, @RequestParam String roomId) {
        System.out.println("foreceLeave");
        User user = userManagementService.getUser(userId);

        if(screenSharingService.isScreenSharingId(userId)) {
            screenSharingService.stopSharing(roomId);
            LeaveResponse response = new LeaveResponse(SignalType.LEAVE, userId, StreamType.SCREEN);
            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/leave", response);
        }
        roomsManagementService.removeParticipant(roomId, user);
        LeaveResponse leaveResponse = new LeaveResponse(SignalType.LEAVE, userId, StreamType.USER);
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/leave", leaveResponse);
    }
}
