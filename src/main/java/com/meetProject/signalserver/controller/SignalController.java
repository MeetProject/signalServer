package com.meetProject.signalserver.controller;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.model.dto.IcePayload;
import com.meetProject.signalserver.model.dto.LeavePayload;
import com.meetProject.signalserver.model.dto.JoinPayload;
import com.meetProject.signalserver.model.dto.RegisterPayload;
import com.meetProject.signalserver.model.dto.SDPPayload;
import com.meetProject.signalserver.model.dto.ScreenPayload;
import com.meetProject.signalserver.orchestration.JoinOrchestrationService;
import com.meetProject.signalserver.orchestration.LeaveOrchestrationService;
import com.meetProject.signalserver.orchestration.ScreenOrchestrationService;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.util.WebSocketUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class SignalController {
    private final SignalMessagingService signalMessagingService;
    private final JoinOrchestrationService joinService;
    private final ScreenOrchestrationService screenService;
    private final LeaveOrchestrationService leaveService;

    public SignalController(SignalMessagingService signalMessagingService,  LeaveOrchestrationService leaveService,  JoinOrchestrationService joinService,  ScreenOrchestrationService screenService) {
        this.signalMessagingService = signalMessagingService;
        this.joinService = joinService;
        this.leaveService = leaveService;
        this.screenService = screenService;
    }

    @MessageMapping("/signal/join")
    public void join(@Payload JoinPayload joinPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        joinService.joinRoom(userId, joinPayload.roomId());
    }

    @MessageMapping("/signal/offer")
    public void offer(@Payload SDPPayload sdpPayload, SimpMessageHeaderAccessor header) {
        String fromUserId = WebSocketUtils.getUserId(header.getUser());
        signalMessagingService.sendSDP(sdpPayload.toUserId(), fromUserId, sdpPayload.fromUserSDP(), sdpPayload.streamType(), SignalType.OFFER);
    }

    @MessageMapping("/signal/answer")
    public void answer(@Payload SDPPayload sdpPayload, SimpMessageHeaderAccessor header) {
        String fromUserId = WebSocketUtils.getUserId(header.getUser());
        signalMessagingService.sendSDP(sdpPayload.toUserId(), fromUserId, sdpPayload.fromUserSDP(), sdpPayload.streamType(), SignalType.ANSWER);
    }

    @MessageMapping("/signal/ice")
    public void ice(@Payload IcePayload icePayload, SimpMessageHeaderAccessor header) {
        String fromUserId = WebSocketUtils.getUserId(header.getUser());
        signalMessagingService.sendICE(icePayload.toUserId(), fromUserId, icePayload.fromCandidate(), icePayload.streamType());
    }

    @MessageMapping("/signal/leave")
    public void leave(@Payload LeavePayload leavePayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        leaveService.leaveUser(userId, leavePayload.streamType());
    }

    @MessageMapping("/signal/screen")
    @SendToUser("/queue/signal/screen")
    public void screen(@Payload ScreenPayload screenPayload, SimpMessageHeaderAccessor header) {
        String UserId = WebSocketUtils.getUserId(header.getUser());
        screenService.shareScreen(UserId, screenPayload.roomId());
    }
}
