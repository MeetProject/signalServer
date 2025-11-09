package com.meetProject.signalserver.controller;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.model.dto.JoinPayload;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.model.dto.JoinResponse;
import com.meetProject.signalserver.service.RoomsManagementService;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class SignalController {
    private final RoomsManagementService roomsManagementService;
    private final SimpMessagingTemplate messagingTemplate;

    public SignalController(RoomsManagementService roomsManagementService, SimpMessagingTemplate messagingTemplate) {
        this.roomsManagementService = roomsManagementService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/register")
    public void register(SimpMessageHeaderAccessor headers) {
        Principal principal = headers.getUser();
        if (principal == null) {
            throw new RuntimeException("Principal is null");
        }

        String userId = principal.getName();
        messagingTemplate.convertAndSendToUser(userId, "/queue/userId", Map.of("userId", userId));
    }

    @MessageMapping("/signal/join")
    public void join(@Payload JoinPayload joinPayload) {
        String targetRoomId = joinPayload.getRoomId();
        System.out.println(joinPayload.getRoomId());
        List<User> participants = roomsManagementService.getParticipants(targetRoomId);
        JoinResponse joinResponse = new JoinResponse(SignalType.JOIN, targetRoomId, participants);
        messagingTemplate.convertAndSendToUser(joinPayload.getUserId(), "/queue/signal/join", joinResponse);
    }
}
