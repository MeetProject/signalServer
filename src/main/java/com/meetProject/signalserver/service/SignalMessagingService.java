package com.meetProject.signalserver.service;

import com.meetProject.signalserver.constant.Emoji;
import com.meetProject.signalserver.constant.StreamType;
import com.meetProject.signalserver.model.MediaOption;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.model.dto.ChatResponse;
import com.meetProject.signalserver.model.dto.DeviceResponse;
import com.meetProject.signalserver.model.dto.EmojiResponse;
import com.meetProject.signalserver.model.dto.ErrorResponse;
import com.meetProject.signalserver.model.dto.HandUpResponse;
import com.meetProject.signalserver.model.dto.IceResponse;
import com.meetProject.signalserver.model.dto.JoinResponse;
import com.meetProject.signalserver.model.dto.LeaveResponse;
import com.meetProject.signalserver.model.dto.AnswerResponse;
import com.meetProject.signalserver.model.dto.OfferResponse;
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

    public void sendJoin(String userId, JoinResponse joinResponse) {
        sendSignal(userId, joinResponse);
    }

    public void sendOffer(String toUserId, String fromUserId, String fromUserSDP, StreamType streamType, MediaOption mediaOption) {
        User user = userService.getUser(fromUserId);
        boolean isScreenSender = screenSharingService.isSharing(fromUserId, user.roomId());
        OfferResponse response = new OfferResponse(fromUserId, user, fromUserSDP, streamType, isScreenSender, mediaOption);
        sendSignal(toUserId, response);
    }

    public void sendAnswer(String toUserId, String fromUserId, String fromUserSDP, StreamType streamType, MediaOption mediaOption) {
        User user = userService.getUser(toUserId);
        boolean isScreenSender = screenSharingService.isSharing(toUserId, user.roomId());
        AnswerResponse answerResponse = new AnswerResponse(fromUserId, fromUserSDP, streamType, isScreenSender, mediaOption);
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
        System.out.println(userId);
        ChatResponse response = new ChatResponse(userId, message, System.currentTimeMillis());
        sendTopic(roomId, response);
    }

    public void sendEmoji(String roomId, String userId, Emoji emoji) {
        System.out.println(userId);
        EmojiResponse response = new EmojiResponse(userId, emoji, System.currentTimeMillis());
        sendTopic(roomId, response);
    }

    public void sendDevice(String roomId, String userId, MediaOption mediaOption) {
        DeviceResponse response = new DeviceResponse(userId, mediaOption);
        sendTopic(roomId, response);
    }

    public void sendHandUp(String roomId, String userId, boolean isHandUp) {
        HandUpResponse response = new HandUpResponse(userId, isHandUp);
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
