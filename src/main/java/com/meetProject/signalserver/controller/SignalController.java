package com.meetProject.signalserver.controller;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.model.dto.IcePayload;
import com.meetProject.signalserver.model.dto.IceResponse;
import com.meetProject.signalserver.model.dto.JoinPayload;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.model.dto.JoinResponse;
import com.meetProject.signalserver.model.dto.SDPResponse;
import com.meetProject.signalserver.model.dto.RegisterResponse;
import com.meetProject.signalserver.model.dto.SDPPayload;
import com.meetProject.signalserver.service.RoomsManagementService;
import java.security.Principal;
import java.util.List;
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
        /* 유저서비스에 정보 등록 */
        return new RegisterResponse(SignalType.REGISTER, principal.getName());
    }

    @MessageMapping("/signal/join")
    @SendToUser("/queue/signal/join")
    public JoinResponse join(@Payload JoinPayload joinPayload) {
        String targetRoomId = joinPayload.roomId();
        List<User> participants = roomsManagementService.getParticipants(targetRoomId);
        /* 사용자 추가 => UserService에 현재 유저 정보 가져오기 => User객체 생성 */
        return new JoinResponse(SignalType.JOIN, targetRoomId, participants);
    }

    @MessageMapping("/signal/offer")
    public void offer(@Payload SDPPayload sdpPayload) {
        String toUserId = sdpPayload.toUserId();
        SDPResponse offerResponse = new SDPResponse(SignalType.OFFER, sdpPayload.fromUserId(), sdpPayload.fromUserSDP());
        messagingTemplate.convertAndSendToUser(toUserId, "/queue/signal/offer", offerResponse);
    }

    @MessageMapping("/signal/answer")
    public void answer(@Payload SDPPayload sdpPayload) {
        String toUserId = sdpPayload.toUserId();
        SDPResponse answerResponse = new SDPResponse(SignalType.ANSWER, sdpPayload.fromUserId(), sdpPayload.fromUserSDP());
        messagingTemplate.convertAndSendToUser(toUserId, "/queue/signal/answer", answerResponse);
    }

    @MessageMapping("/signal/ice")
    public void ice(@Payload IcePayload icePayload) {
        String toUserId = icePayload.toUserId();
        IceResponse iceResponse = new IceResponse(SignalType.ICE, icePayload.fromUserId(), icePayload.fromCandidate());
        messagingTemplate.convertAndSendToUser(toUserId, "/queue/signal/ice", iceResponse);
    }
}
