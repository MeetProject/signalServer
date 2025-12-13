package com.meetProject.signalserver.controller;

import com.meetProject.signalserver.model.dto.AnswerPayload;
import com.meetProject.signalserver.model.dto.IcePayload;
import com.meetProject.signalserver.model.dto.JoinPayload;
import com.meetProject.signalserver.model.dto.OfferPayload;
import com.meetProject.signalserver.model.dto.TrackPayload;
import com.meetProject.signalserver.orchestration.JoinOrchestrationService;
import com.meetProject.signalserver.orchestration.LeaveOrchestrationService;
import com.meetProject.signalserver.orchestration.ScreenOrchestrationService;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.service.UserService;
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
    private final UserService userService;

    public SignalController(SignalMessagingService signalMessagingService,  LeaveOrchestrationService leaveService,  JoinOrchestrationService joinService,  ScreenOrchestrationService screenService, UserService userService) {
        this.signalMessagingService = signalMessagingService;
        this.joinService = joinService;
        this.leaveService = leaveService;
        this.screenService = screenService;
        this.userService = userService;
    }

    @MessageMapping("/signal/join")
    public void join(@Payload JoinPayload joinPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        joinService.joinRoom(userId, joinPayload.roomId(), joinPayload.mediaOption());
    }

    @MessageMapping("/signal/offer")
    public void offer(@Payload OfferPayload offerPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = userService.getRoomId(userId);
        signalMessagingService.sendOffer(userId, offerPayload.userId(), roomId, offerPayload.sdp());
    }

    @MessageMapping("/signal/answer")
    public void answer(@Payload AnswerPayload answerPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        signalMessagingService.sendAnswer(userId, answerPayload.userId(), answerPayload.sdp());
    }

    @MessageMapping("/signal/ice")
    public void ice(@Payload IcePayload icePayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        signalMessagingService.sendICE(userId, icePayload.userId(), icePayload.ice());
    }

    @MessageMapping("/signal/leave")
    public void leave(SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        leaveService.leaveUser(userId);
    }

    @MessageMapping("/signal/track")
    public void track(@Payload TrackPayload trackPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = userService.getRoomId(userId);
        signalMessagingService.sendTrack(userId, trackPayload.userId(), roomId, trackPayload.track());
    }

}
