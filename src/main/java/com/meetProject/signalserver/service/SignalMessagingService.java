package com.meetProject.signalserver.service;

import com.meetProject.signalserver.constant.Emoji;
import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.constant.StreamType;
import com.meetProject.signalserver.model.dto.ChatResponse;
import com.meetProject.signalserver.model.dto.EmojiResponse;
import com.meetProject.signalserver.model.dto.IceResponse;
import com.meetProject.signalserver.model.dto.LeaveResponse;
import com.meetProject.signalserver.model.dto.SDPResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SignalMessagingService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ScreenSharingService screenSharingService;

    public SignalMessagingService(SimpMessagingTemplate messagingTemplate, ScreenSharingService screenSharingService) {
        this.messagingTemplate = messagingTemplate;
        this.screenSharingService = screenSharingService;
    }

    public void sendSDP(String toUserId, String fromUserId, String fromUserSDP, StreamType streamType, SignalType type) {
        Boolean isScreenSender = screenSharingService.isScreenSharingId(toUserId);
        SDPResponse answerResponse = new SDPResponse(type, fromUserId, fromUserSDP, streamType, isScreenSender);
        messagingTemplate.convertAndSendToUser(toUserId, "/queue/signal/" + type.name().toLowerCase(), answerResponse);
    }

    public void sendICE(String toUserId, String fromUserId, String fromUserICE, StreamType streamType) {
        IceResponse iceResponse = new IceResponse(SignalType.ICE, fromUserId, fromUserICE, streamType);
        messagingTemplate.convertAndSendToUser(toUserId, "/queue/signal/ice", iceResponse);
    }

    public void sendLeave(String roomId, String userId, StreamType type) {
        LeaveResponse response = new LeaveResponse(SignalType.LEAVE, userId, type);
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/leave", response);
    }

    public void sendChat(String roomId, String userId, String message) {
        ChatResponse response = new ChatResponse(userId, message, System.currentTimeMillis());
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/chat", response);
    }

    public void sendEmoji(String roomId, String userId, Emoji emoji) {
        EmojiResponse response = new EmojiResponse(userId, emoji);
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/emoji", response);
    }
}
