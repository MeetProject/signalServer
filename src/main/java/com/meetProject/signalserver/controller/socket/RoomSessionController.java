package com.meetProject.signalserver.controller.socket;

import com.meetProject.signalserver.model.dto.socket.RoomSessionDto.JoinPayload;
import com.meetProject.signalserver.orchestration.JoinOrchestrationService;
import com.meetProject.signalserver.orchestration.LeaveOrchestrationService;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.service.UserService;
import com.meetProject.signalserver.util.WebSocketUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class RoomSessionController {
    private final SignalMessagingService signalMessagingService;
    private final JoinOrchestrationService joinService;
    private final LeaveOrchestrationService leaveService;
    private final UserService userService;

    public RoomSessionController(SignalMessagingService signalMessagingService,  LeaveOrchestrationService leaveService,  JoinOrchestrationService joinService, UserService userService) {
        this.signalMessagingService = signalMessagingService;
        this.joinService = joinService;
        this.leaveService = leaveService;
        this.userService = userService;
    }

    @MessageMapping("/signal/join")
    public void join(@Payload JoinPayload joinPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        joinService.joinRoom(userId, joinPayload.roomId(), joinPayload.mediaOption());
    }


    @MessageMapping("/signal/leave")
    public void leave(SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        leaveService.leaveUser(userId);
    }

}
