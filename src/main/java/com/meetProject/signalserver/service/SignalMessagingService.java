package com.meetProject.signalserver.service;

import com.meetProject.signalserver.constant.Emoji;
import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.model.MediaOption;
import com.meetProject.signalserver.model.TrackInfo;
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
import com.meetProject.signalserver.model.dto.TrackResponse;
import com.meetProject.signalserver.model.dto.envelop.SignalEnvelope;
import com.meetProject.signalserver.model.dto.envelop.TopicEnvelope;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class SignalMessagingService {
    private final SignallingSessionService signallingSessionService;
    private final WebSocketUserService webSocketUserService;

    public SignalMessagingService(SignallingSessionService signallingSessionService, WebSocketUserService webSocketUserService) {
        this.signallingSessionService = signallingSessionService;
        this.webSocketUserService = webSocketUserService;
    }

    public void sendJoin(String userId, String roomId, User user, List<User> users, MediaOption mediaOption) {
        JoinResponse joinResponse = new JoinResponse(userId, roomId, users);
        SignalEnvelope<JoinResponse> payload = new SignalEnvelope<>(SignalType.JOIN, joinResponse);
        sendSignal(userId, payload);

        ParticipantResponse participantResponse = new ParticipantResponse(userId, roomId, user, mediaOption);
        TopicEnvelope<ParticipantResponse> topicPayload = new TopicEnvelope<>(TopicType.PARTICIPANT, participantResponse);
        sendTopic(userId, roomId, topicPayload);
    }

    public void sendOffer(String senderId, String targetId, String roomId, String sdp) {
        System.out.println("offerTo: " + targetId);
        withMediaServerConnected(senderId, () -> {
            OfferResponse response = new OfferResponse(targetId, roomId, sdp);
            SignalEnvelope<OfferResponse> payload = new SignalEnvelope<>(SignalType.OFFER, response);
            sendSignalConsideringSender(senderId, targetId, payload);
        });
    }

    public void sendAnswer(String senderId, String targetId, String sdp, String roomId) {
        withMediaServerConnected(senderId, () -> {
            AnswerResponse answerResponse = new AnswerResponse(targetId, sdp, roomId);
            SignalEnvelope<AnswerResponse> payload = new SignalEnvelope<>(SignalType.ANSWER, answerResponse);
            sendSignalConsideringSender(senderId, targetId, payload);
        });
    }

    public void sendICE(String senderId, String targetId, String ice) {
        withMediaServerConnected(senderId, () -> {
            IceResponse iceResponse = new IceResponse(targetId, ice);
            SignalEnvelope<IceResponse> payload = new SignalEnvelope<>(SignalType.ICE, iceResponse);
            sendSignalConsideringSender(senderId, targetId, payload);
        });
    }

    public void sendTrack(String senderId, String userId, String roomId, Map<String, TrackInfo> trackInfo) {
        withMediaServerConnected(senderId, () -> {
            TrackResponse trackResponse = new TrackResponse(userId, roomId, trackInfo);
            SignalEnvelope<TrackResponse> payload = new SignalEnvelope<>(SignalType.TRACK, trackResponse);
            sendSignalConsideringSender(senderId, userId, payload);
        });
    }

    public void sendLeave(String userId, String roomId) {
        withMediaServerConnected(userId, () -> {
            LeaveResponse response = new LeaveResponse(userId);
            TopicEnvelope<LeaveResponse> topicPayload = new TopicEnvelope<>(TopicType.LEAVE, response);
            sendTopic(userId, roomId, topicPayload);

            SignalEnvelope<LeaveResponse> payload = new SignalEnvelope<>(SignalType.LEAVE, response);
            sendSignal("mediaServer", payload);
        });
    }

    public void sendChat(String roomId, String userId, String message) {
        ChatResponse response = new ChatResponse(userId, message, System.currentTimeMillis());
        TopicEnvelope<ChatResponse> topicPayload = new TopicEnvelope<>(TopicType.CHAT, response);
        sendTopic(userId, roomId, topicPayload);
    }

    public void sendEmoji(String roomId, String userId, Emoji emoji) {
        EmojiResponse response = new EmojiResponse(userId, emoji, System.currentTimeMillis());
        TopicEnvelope<EmojiResponse> topicPayload = new TopicEnvelope<>(TopicType.EMOJI, response);
        sendTopic(userId, roomId, topicPayload);
    }

    public void sendDevice(String roomId, String userId, MediaOption mediaOption) {
        DeviceResponse response = new DeviceResponse(userId, mediaOption);
        TopicEnvelope<DeviceResponse> payload = new TopicEnvelope<>(TopicType.DEVICE, response);
        sendTopic(userId, roomId, payload);
    }

    public void sendHandUp(String roomId, String userId, boolean isHandUp) {
        HandUpResponse response = new HandUpResponse(userId, isHandUp);
        TopicEnvelope<HandUpResponse> topicPayload = new TopicEnvelope<>(TopicType.HANDUP, response);
        sendTopic(userId, roomId, topicPayload);
    }

    public void sendError(String userId, ErrorResponse errorResponse) {
        SignalEnvelope<ErrorResponse> payload = new SignalEnvelope<>(SignalType.ERROR, errorResponse);
        sendSignal(userId, payload);
    }

    private void sendSignal(String userId, SignalEnvelope<?> payload) {
        signallingSessionService.sendToUser(userId, payload);
    }

    private void sendTopic(String userId, String roomId, TopicEnvelope<?> payload) {
        signallingSessionService.sendToRoom(userId, roomId, payload);
    }

    private void withMediaServerConnected(String senderId, Runnable action) {
        if (!webSocketUserService.isUserConnected("mediaServer")) {
            ErrorResponse errorResponse = new ErrorResponse(
                    ErrorCode.E002,
                    ErrorMessage.NOT_CONNECT_MEDIA_SERVER
            );
            sendError(senderId, errorResponse);
            return;
        }
        action.run();
    }

    private void sendSignalConsideringSender(String senderId, String targetId, SignalEnvelope<?> response) {
        if (senderId.equals("mediaServer")) {
            sendSignal(targetId, response);
            return;
        }
        sendSignal("mediaServer", response);
    }
}
