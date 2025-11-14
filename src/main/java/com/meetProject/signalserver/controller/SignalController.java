package com.meetProject.signalserver.controller;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.constant.StreamType;
import com.meetProject.signalserver.model.ScreenSharing;
import com.meetProject.signalserver.model.dto.ForceLeavePayload;
import com.meetProject.signalserver.model.dto.IcePayload;
import com.meetProject.signalserver.model.dto.IceResponse;
import com.meetProject.signalserver.model.dto.LeavePayload;
import com.meetProject.signalserver.model.dto.LeaveResponse;
import com.meetProject.signalserver.model.dto.JoinPayload;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.model.dto.JoinResponse;
import com.meetProject.signalserver.model.dto.RegisterPayload;
import com.meetProject.signalserver.model.dto.SDPResponse;
import com.meetProject.signalserver.model.dto.RegisterResponse;
import com.meetProject.signalserver.model.dto.SDPPayload;
import com.meetProject.signalserver.model.dto.ScreenPayload;
import com.meetProject.signalserver.model.dto.ScreenResponse;
import com.meetProject.signalserver.service.RoomsManagementService;
import com.meetProject.signalserver.service.ScreenSharingService;
import com.meetProject.signalserver.service.UserManagementService;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class SignalController {
    private final RoomsManagementService roomsManagementService;
    private final UserManagementService userManagementService;
    private final ScreenSharingService screenSharingService;
    private final SimpMessagingTemplate messagingTemplate;

    public SignalController(SimpMessagingTemplate messagingTemplate, RoomsManagementService roomsManagementService,
                            UserManagementService userManagementService, ScreenSharingService screenSharingService) {
        this.messagingTemplate = messagingTemplate;
        this.roomsManagementService = roomsManagementService;
        this.userManagementService = userManagementService;
        this.screenSharingService = screenSharingService;
    }

    @MessageMapping("/register")
    @SendToUser("/queue/userId")
    public RegisterResponse register(@Payload RegisterPayload registerPayload, SimpMessageHeaderAccessor headers) {
        Principal principal = headers.getUser();
        if (principal == null) {
            throw new RuntimeException("Principal is null");
        }

        String userId = principal.getName();
        User user = new User(registerPayload.userName(), registerPayload.userColor(), userId);
        userManagementService.addUser(userId, user);
        return new RegisterResponse(SignalType.REGISTER, principal.getName());
    }

    @MessageMapping("/signal/join")
    @SendToUser("/queue/signal/join")
    public JoinResponse join(@Payload JoinPayload joinPayload, SimpMessageHeaderAccessor headers) {
        String targetRoomId = joinPayload.roomId();
        String userId = Objects.requireNonNull(headers.getUser()).getName();

        List<User> participants = roomsManagementService.getParticipants(targetRoomId);

        User user = userManagementService.getUser(userId);
        roomsManagementService.addParticipant(targetRoomId, user);
        ScreenSharing screenSharing = screenSharingService.getScreenSharing(joinPayload.roomId());
        String screenOwnerId = screenSharing == null ? null : screenSharing.ownerId();
        return new JoinResponse(SignalType.JOIN, targetRoomId, participants, screenOwnerId);
    }

    @MessageMapping("/signal/offer")
    public void offer(@Payload SDPPayload sdpPayload, SimpMessageHeaderAccessor header) {
        String toUserId = sdpPayload.toUserId();
        StreamType streamType = sdpPayload.streamType();
        String fromUserId = header.getUser().getName();
        Boolean isScreenSender = screenSharingService.isScreenSharingId(toUserId);

        SDPResponse offerResponse = new SDPResponse(SignalType.OFFER, fromUserId, sdpPayload.fromUserSDP(), streamType, isScreenSender);
        messagingTemplate.convertAndSendToUser(toUserId, "/queue/signal/offer", offerResponse);
    }

    @MessageMapping("/signal/answer")
    public void answer(@Payload SDPPayload sdpPayload, SimpMessageHeaderAccessor header) {
        String fromUserId = header.getUser().getName();
        String toUserId = sdpPayload.toUserId();
        String fromUserSDP = sdpPayload.fromUserSDP();
        StreamType streamType = sdpPayload.streamType();

        Boolean isScreenSender = screenSharingService.isScreenSharingId(toUserId);

        SDPResponse answerResponse = new SDPResponse(SignalType.ANSWER, fromUserId, fromUserSDP, streamType, isScreenSender);
        messagingTemplate.convertAndSendToUser(toUserId, "/queue/signal/answer", answerResponse);
    }

    @MessageMapping("/signal/ice")
    public void ice(@Payload IcePayload icePayload, SimpMessageHeaderAccessor header) {
        String fromUserId = header.getUser().getName();
        String fromCandidate =  icePayload.fromCandidate();
        StreamType streamType = icePayload.streamType();
        String toUserId = icePayload.toUserId();
        IceResponse iceResponse = new IceResponse(SignalType.ICE, fromUserId, fromCandidate, streamType);
        messagingTemplate.convertAndSendToUser(toUserId, "/queue/signal/ice", iceResponse);
    }

    @MessageMapping("/signal/leave")
    public void leave(@Payload LeavePayload leavePayload, SimpMessageHeaderAccessor header) {
        String fromUserId = Objects.requireNonNull(header.getUser()).getName();
        StreamType streamType = leavePayload.streamType();
        String roomId = leavePayload.roomId();

        if(streamType.equals(StreamType.SCREEN)) {
            ScreenSharing screenSharing = screenSharingService.getScreenSharing(roomId);
            if(screenSharing.ownerId().equals(fromUserId)) {
                screenSharingService.stopSharing(roomId);
                LeaveResponse leaveResponse = new LeaveResponse(SignalType.LEAVE, fromUserId, streamType);
                messagingTemplate.convertAndSend("/topic/room/" + roomId + "/leave", leaveResponse);
            }
            return;
        }

        User user = userManagementService.getUser(fromUserId);

        if(screenSharingService.isScreenSharingId(fromUserId)) {
            screenSharingService.stopSharing(roomId);
            LeaveResponse response = new LeaveResponse(SignalType.LEAVE, fromUserId, StreamType.SCREEN);
            messagingTemplate.convertAndSend("/topic/room/" + roomId + "/leave", response);
        }
        roomsManagementService.removeParticipant(roomId, user);
        LeaveResponse leaveResponse = new LeaveResponse(SignalType.LEAVE, fromUserId, streamType);
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/leave", leaveResponse);
    }

    @MessageMapping("/signal/screen")
    @SendToUser("/queue/signal/screen")
    public ScreenResponse screen(@Payload ScreenPayload screenPayload, SimpMessageHeaderAccessor header) {
        String roomId = screenPayload.roomId();
        String ownerId = header.getUser().getName();

        List<User> participants = roomsManagementService.getParticipants(roomId).stream().filter((participant) -> !participant.userId().equals(ownerId))
                .toList();

        ScreenSharing screenSharing = new ScreenSharing(roomId, ownerId);
        screenSharingService.startSharing(roomId, screenSharing);
        return new ScreenResponse(SignalType.SCREEN, ownerId, participants);

    }
}
