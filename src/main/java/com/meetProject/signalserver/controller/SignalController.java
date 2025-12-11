package com.meetProject.signalserver.controller;

import com.meetProject.signalserver.model.dto.AnswerPayload;
import com.meetProject.signalserver.model.dto.IcePayload;
import com.meetProject.signalserver.model.dto.JoinPayload;
import com.meetProject.signalserver.model.dto.OfferPayload;
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
        joinService.joinRoom(userId, joinPayload.roomId(), joinPayload.mediaOption());
    }

    @MessageMapping("/signal/offer")
    public void offer(@Payload OfferPayload offerPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        signalMessagingService.sendOffer(userId, offerPayload.roomId(), offerPayload.sdp());
    }

    @MessageMapping("/signal/answer")
    public void answer(@Payload AnswerPayload answerPayload, SimpMessageHeaderAccessor header) {
        signalMessagingService.sendAnswer(answerPayload.userId(), answerPayload.sdp());
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

    @MessageMapping("/signal/screen")
    @SendToUser("/queue/signal/screen")
    public void screen(@Payload ScreenPayload screenPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        screenService.shareScreen(userId, screenPayload.trackId());
    }
}
