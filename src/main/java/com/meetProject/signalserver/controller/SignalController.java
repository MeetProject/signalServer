package com.meetProject.signalserver.controller;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.constant.StreamType;
import com.meetProject.signalserver.model.ScreenSharing;
import com.meetProject.signalserver.model.dto.IcePayload;
import com.meetProject.signalserver.model.dto.LeavePayload;
import com.meetProject.signalserver.model.dto.JoinPayload;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.model.dto.JoinResponse;
import com.meetProject.signalserver.model.dto.RegisterPayload;
import com.meetProject.signalserver.model.dto.RegisterResponse;
import com.meetProject.signalserver.model.dto.SDPPayload;
import com.meetProject.signalserver.model.dto.ScreenPayload;
import com.meetProject.signalserver.model.dto.ScreenResponse;
import com.meetProject.signalserver.service.RoomsManagementService;
import com.meetProject.signalserver.service.ScreenSharingService;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.service.UserManagementService;
import com.meetProject.signalserver.util.WebSocketUtils;
import java.security.Principal;
import java.util.List;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
public class SignalController {
    private final RoomsManagementService roomsManagementService;
    private final UserManagementService userManagementService;
    private final ScreenSharingService screenSharingService;
    private final SignalMessagingService signalMessagingService;

    public SignalController(RoomsManagementService roomsManagementService,
                            UserManagementService userManagementService, ScreenSharingService screenSharingService, SignalMessagingService signalMessagingService) {
        this.roomsManagementService = roomsManagementService;
        this.userManagementService = userManagementService;
        this.screenSharingService = screenSharingService;
        this.signalMessagingService = signalMessagingService;
    }

    @MessageMapping("/register")
    @SendToUser("/queue/userId")
    public RegisterResponse register(@Payload RegisterPayload registerPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        User user = new User(registerPayload.userName(), registerPayload.userColor(), userId, null);
        userManagementService.addUser(userId, user);
        return new RegisterResponse(SignalType.REGISTER, userId);
    }

    @MessageMapping("/signal/join")
    @SendToUser("/queue/signal/join")
    public JoinResponse join(@Payload JoinPayload joinPayload, SimpMessageHeaderAccessor header) {
        String targetRoomId = joinPayload.roomId();
        String userId = WebSocketUtils.getUserId(header.getUser());

        if(userManagementService.getUser(userId).roomId() != null) {
            throw new IllegalArgumentException("User already joined room");
        }

        List<User> participants = roomsManagementService.getParticipants(targetRoomId).stream()
                .map(userManagementService::getUser)
                .toList();
        roomsManagementService.addParticipant(targetRoomId, userId);
        userManagementService.updateRoomStatus(userId, targetRoomId);
        String screenOwnerId = getScreenOwner(joinPayload.roomId());
        return new JoinResponse(SignalType.JOIN, targetRoomId, participants, screenOwnerId);
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
        String fromUserId = WebSocketUtils.getUserId(header.getUser());
        StreamType streamType = leavePayload.streamType();
        String roomId = leavePayload.roomId();
        String screenOwnerId = getScreenOwner(roomId);

        if (screenOwnerId != null && screenOwnerId.equals(fromUserId)) {
            screenSharingService.stopSharing(roomId);
            signalMessagingService.sendLeave(roomId, fromUserId, StreamType.SCREEN);
        }

        if (streamType.equals(StreamType.USER)) {
            roomsManagementService.removeParticipant(roomId, fromUserId);
            userManagementService.updateRoomStatus(fromUserId, null);
            signalMessagingService.sendLeave(roomId, fromUserId, streamType);
        }
    }

    @MessageMapping("/signal/screen")
    @SendToUser("/queue/signal/screen")
    public ScreenResponse screen(@Payload ScreenPayload screenPayload, SimpMessageHeaderAccessor header) {
        String roomId = screenPayload.roomId();
        String ownerId = WebSocketUtils.getUserId(header.getUser());

        List<String> participants = roomsManagementService.getParticipants(roomId).stream()
                .filter((participant) -> !participant.equals(ownerId))
                .toList();

        ScreenSharing screenSharing = new ScreenSharing(roomId, ownerId);
        screenSharingService.startSharing(roomId, screenSharing);
        return new ScreenResponse(SignalType.SCREEN, ownerId, participants);

    }


    private String getScreenOwner(String roomId) {
        ScreenSharing screenSharing = screenSharingService.getScreenSharing(roomId);
        if(screenSharing == null) {
            return null;
        }
        return screenSharing.ownerId();
    }
}
