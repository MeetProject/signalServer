package com.meetProject.signalserver.service;

import com.meetProject.signalserver.constant.Emoji;
import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.constant.ErrorMessage;
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
import com.meetProject.signalserver.model.dto.ParticipantResponse;
import com.meetProject.signalserver.model.dto.ScreenResponse;
import com.meetProject.signalserver.model.dto.SignalResponse;
import com.meetProject.signalserver.model.dto.TopicResponse;
import java.util.List;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SignalMessagingService {
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketUserService webSocketUserService;

    public SignalMessagingService(SimpMessagingTemplate messagingTemplate, WebSocketUserService webSocketUserService) {
        this.messagingTemplate = messagingTemplate;
        this.webSocketUserService = webSocketUserService;
    }

    public void sendJoin(String userId, String roomId, List<User> users, MediaOption mediaOption) {
        JoinResponse joinResponse = new JoinResponse(userId, roomId, users);
        sendSignal(userId, joinResponse);

        ParticipantResponse participantResponse = new ParticipantResponse(userId, mediaOption);
        sendTopic(roomId, participantResponse);
    }

    public void sendOffer(String senderId, String targetId, String roomId, String sdp) {
        if(!webSocketUserService.isUserConnected("mediaServer")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.E002, ErrorMessage.NOT_CONNECT_MEDIA_SERVER);
            sendError(senderId, errorResponse);
            return;
        }
        OfferResponse response = new OfferResponse(targetId, roomId, sdp);

        if(senderId.equals("mediaServer")) {
            sendSignal(targetId, response);
            return;
        }
        sendSignal("mediaServer", response);
    }

    public void sendAnswer(String senderId, String targetId, String sdp) {
        AnswerResponse answerResponse = new AnswerResponse(targetId, sdp);
        if(senderId.equals("mediaServer")) {
            sendSignal(targetId, answerResponse);
            return;
        }
        sendSignal("mediaServer", answerResponse);
    }

    public void sendICE(String senderId, String targetId, String ice) {
        IceResponse iceResponse = new IceResponse(targetId, ice);
        if(senderId.equals("mediaServer")) {
            sendSignal(targetId, iceResponse);
            return;
        }
        sendSignal("mediaServer", iceResponse);
    }

    public void sendLeave(String userId, String roomId) {
        LeaveResponse response = new LeaveResponse(userId);
        sendTopic(roomId, response);
        sendSignal("mediaServer", response);
    }

    public void shareScreen(String userId, String trackId) {
        ScreenResponse screenResponse = new ScreenResponse(userId, trackId);
        sendSignal("mediaServer", screenResponse);
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
        messagingTemplate.convertAndSendToUser(userId, "/queue/signal/" + payload.getSignalType().name().toLowerCase(), payload);
    }

    private void sendTopic(String roomId, TopicResponse payload) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/" + payload.getTopicType().name().toLowerCase(), payload);
    }

}
