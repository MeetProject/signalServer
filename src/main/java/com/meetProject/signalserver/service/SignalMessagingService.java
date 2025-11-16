package com.meetProject.signalserver.service;

import com.meetProject.signalserver.constant.Emoji;
import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.constant.StreamType;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.model.dto.ChatResponse;
import com.meetProject.signalserver.model.dto.EmojiResponse;
import com.meetProject.signalserver.model.dto.ErrorResponse;
import com.meetProject.signalserver.model.dto.IceResponse;
import com.meetProject.signalserver.model.dto.JoinResponse;
import com.meetProject.signalserver.model.dto.LeaveResponse;
import com.meetProject.signalserver.model.dto.RegisterResponse;
import com.meetProject.signalserver.model.dto.SDPResponse;
import com.meetProject.signalserver.model.dto.ScreenResponse;
import com.meetProject.signalserver.model.dto.SignalResponse;
import com.meetProject.signalserver.model.dto.TopicResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SignalMessagingService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ScreenSharingService screenSharingService;
    private final UserService userService;

    public SignalMessagingService(SimpMessagingTemplate messagingTemplate, ScreenSharingService screenSharingService,
                                  UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.screenSharingService = screenSharingService;
        this.userService = userService;
    }

    public void sendRegister(String userId, RegisterResponse response) {
        sendSignal(userId, response);
    }

    public void sendJoin(String userId, JoinResponse joinResponse) {
        sendSignal(userId, joinResponse);
    }

    public void sendSDP(String toUserId, String fromUserId, String fromUserSDP, StreamType streamType,
                        SignalType type) {
        User user = userService.getUser(toUserId);
        boolean isScreenSender = screenSharingService.isSharing(toUserId, user.roomId());
        SDPResponse answerResponse = new SDPResponse(type, fromUserId, fromUserSDP, streamType, isScreenSender);
        sendSignal(toUserId, answerResponse);
    }

    public void sendICE(String toUserId, String fromUserId, String fromUserICE, StreamType streamType) {
        IceResponse iceResponse = new IceResponse(fromUserId, fromUserICE, streamType);
        sendSignal(toUserId, iceResponse);
    }

    public void sendLeave(String roomId, String userId, StreamType type) {
        LeaveResponse response = new LeaveResponse(userId, type);
        sendTopic(roomId, response);
    }

    public void shareScreen(String userId, ScreenResponse screenResponse) {
        sendSignal(userId, screenResponse);
    }

    public void sendChat(String roomId, String userId, String message) {
        ChatResponse response = new ChatResponse(userId, message, System.currentTimeMillis());
        sendTopic(roomId, response);
    }

    public void sendEmoji(String roomId, String userId, Emoji emoji) {
        EmojiResponse response = new EmojiResponse(userId, emoji);
        sendTopic(roomId, response);
    }

    public void sendError(String userId, ErrorResponse errorResponse) {
        sendSignal(userId, errorResponse);
    }

    private void sendSignal(String userId, SignalResponse payload) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/signal/" + payload.getType().name().toLowerCase(), payload);
    }

    private void sendTopic(String roomId, TopicResponse payload) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/" + payload.getType().name().toLowerCase(), payload);
    }

}
