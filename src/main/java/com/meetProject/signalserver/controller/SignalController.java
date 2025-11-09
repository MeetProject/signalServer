package com.meetProject.signalserver.controller;

import com.meetProject.signalserver.model.JoinPayload;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.service.RoomsManagementService;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
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
        String targetRoomId = joinPayload.roomId();
        List<User> participants = roomsManagementService.getParticipants(targetRoomId);
        messagingTemplate.convertAndSendToUser(joinPayload.userId(), "/topic/signal/join", participants);
    }
}
