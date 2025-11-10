package com.meetProject.signalserver.controller;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.model.dto.AnswerResponse;
import com.meetProject.signalserver.model.dto.IcePayload;
import com.meetProject.signalserver.model.dto.IceResponse;
import com.meetProject.signalserver.model.dto.JoinPayload;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.model.dto.JoinResponse;
import com.meetProject.signalserver.model.dto.OfferResponse;
import com.meetProject.signalserver.model.dto.RegisterResponse;
import com.meetProject.signalserver.model.dto.SDPPayload;
import com.meetProject.signalserver.service.RoomsManagementService;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
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
    @SendToUser("/queue/userId")
    public RegisterResponse register(SimpMessageHeaderAccessor headers) {
        Principal principal = headers.getUser();
        if (principal == null) {
            throw new RuntimeException("Principal is null");
        }
        return new RegisterResponse(SignalType.REGISTER, principal.getName());
    }

    @MessageMapping("/signal/join")
    @SendToUser("/queue/signal/join")
    public JoinResponse join(@Payload JoinPayload joinPayload) {
        String targetRoomId = joinPayload.roomId();
        List<User> participants = roomsManagementService.getParticipants(targetRoomId);
        return new JoinResponse(SignalType.JOIN, targetRoomId, participants);
    }

    @MessageMapping("/signal/offer")
    public void offer(@Payload SDPPayload sdpPayload) {
        String toUserId = sdpPayload.toUserId();
        OfferResponse offerResponse = new OfferResponse(SignalType.OFFER, sdpPayload.fromUserId(), sdpPayload.fromUserSDP());
        messagingTemplate.convertAndSendToUser(toUserId, "/queue/signal/offer", offerResponse);
    }

    @MessageMapping("/signal/answer")
    public void answer(@Payload SDPPayload sdpPayload) {
        String toUserId = sdpPayload.toUserId();
        AnswerResponse answerResponse = new AnswerResponse(SignalType.ANSWER, sdpPayload.fromUserId(), sdpPayload.fromUserSDP());
        messagingTemplate.convertAndSendToUser(toUserId, "/queue/signal/answer", answerResponse);
    }

    @MessageMapping("/signal/ice")
    public void ice(@Payload IcePayload icePayload) {
        String toUserId = icePayload.toUserId();
        IceResponse iceResponse = new IceResponse(SignalType.ICE, icePayload.fromUserId(), icePayload.fromCandidate());
        messagingTemplate.convertAndSendToUser(toUserId, "/queue/signal/ice", iceResponse);
    }
}
