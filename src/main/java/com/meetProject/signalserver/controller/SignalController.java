package com.meetProject.signalserver.controller;

import com.meetProject.signalserver.model.JoinPayload;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.service.RoomsManagementService;
import java.util.List;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class SignalController {
    private final RoomsManagementService roomsManagementService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public SignalController(RoomsManagementService roomsManagementService, SimpMessagingTemplate messagingTemplate) {
        this.roomsManagementService = roomsManagementService;
        this.simpMessagingTemplate = messagingTemplate;
    }

    @MessageMapping("/signal/join")
    public void join(@Payload JoinPayload joinPayload) {
        String targetRoomId = joinPayload.roomId();
        List<User> participants = roomsManagementService.getParticipants(targetRoomId);
        simpMessagingTemplate.convertAndSendToUser(joinPayload.userId(), "queue/signal/join", participants);
    }
}
