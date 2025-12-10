package com.meetProject.signalserver.service;

import com.meetProject.signalserver.constant.Emoji;
import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.constant.ErrorMessage;
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
        JoinResponse joinResponse = new JoinResponse(roomId, users);
        sendSignal(userId, joinResponse);

        ParticipantResponse participantResponse = new ParticipantResponse(userId, mediaOption);
        sendTopic(roomId, participantResponse);
    }

    public void sendOffer(String userId, String roomId, String sdp) {
        if(!webSocketUserService.isUserConnected("mediaServer")) {
            ErrorResponse errorResponse = new ErrorResponse(ErrorCode.E002, ErrorMessage.NOT_CONNECT_MEDIA_SERVER);
            sendError(userId, errorResponse);
            return;
        }
        OfferResponse response = new OfferResponse(userId, roomId, sdp);
        sendSignal("mediaServer", response);
    }

    public void sendAnswer(String userId, String sdp) {
        AnswerResponse answerResponse = new AnswerResponse(sdp);
        sendSignal(userId, answerResponse);
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
