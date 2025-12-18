package com.meetProject.signalserver.service.socket.application;

import com.meetProject.signalserver.constant.Emoji;
import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.model.MediaOption;
import com.meetProject.signalserver.model.TrackInfo;
import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.model.socket.topic.ChatResponse;
import com.meetProject.signalserver.model.socket.topic.DeviceResponse;
import com.meetProject.signalserver.model.socket.topic.EmojiResponse;
import com.meetProject.signalserver.model.socket.signal.ErrorResponse;
import com.meetProject.signalserver.model.socket.topic.HandUpResponse;
import com.meetProject.signalserver.model.socket.signal.IceResponse;
import com.meetProject.signalserver.model.socket.signal.JoinResponse;
import com.meetProject.signalserver.model.socket.LeaveResponse;
import com.meetProject.signalserver.model.socket.signal.AnswerResponse;
import com.meetProject.signalserver.model.socket.signal.NegotiationResponse;
import com.meetProject.signalserver.model.socket.signal.OfferResponse;
import com.meetProject.signalserver.model.socket.ParticipantResponse;
import com.meetProject.signalserver.model.envelop.SignalEnvelope;
import com.meetProject.signalserver.model.envelop.TopicEnvelope;
import com.meetProject.signalserver.service.domain.UserService;
import com.meetProject.signalserver.service.socket.infrastructure.SignallingSessionService;
import com.meetProject.signalserver.service.socket.infrastructure.WebSocketUserService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class SignalMessagingService {
    private final SignallingSessionService signallingSessionService;
    private final WebSocketUserService webSocketUserService;
    private final UserService userService;

    public SignalMessagingService(SignallingSessionService signallingSessionService, WebSocketUserService webSocketUserService, UserService userService) {
        this.signallingSessionService = signallingSessionService;
        this.webSocketUserService = webSocketUserService;
        this.userService = userService;
    }

    public void sendJoin(String userId, String roomId, List<User> users)
            throws IOException {

        checkMediaServerConnected(userId);
        JoinResponse joinResponse = new JoinResponse(userId, roomId, users);
        SignalEnvelope<JoinResponse> payload = new SignalEnvelope<>(SignalType.JOIN, joinResponse);
        sendSignal(userId, payload);
    }

    public void sendParticipant(String userId, String roomId, User user, MediaOption mediaOption)
            throws IOException {
        ParticipantResponse response = new ParticipantResponse(userId, roomId, user, mediaOption);

        TopicEnvelope<ParticipantResponse> topicPayload = new TopicEnvelope<>(TopicType.PARTICIPANT, response);
        sendTopic(userId, roomId, topicPayload);
    }

    public void sendOffer(String targetId, String sdp, Map<String,TrackInfo> trackInfo) throws IOException {
        OfferResponse response = new OfferResponse(sdp, trackInfo);
        SignalEnvelope<OfferResponse> payload = new SignalEnvelope<>(SignalType.OFFER, response);
        sendSignal(targetId, payload);
    }

    public void sendAnswer(String targetId, String sdp, Map<String, TrackInfo> trackInfo) throws IOException {
        checkMediaServerConnected(targetId);
        String roomId = userService.getRoomId(targetId);
        AnswerResponse response = new AnswerResponse(targetId, roomId, sdp, trackInfo);
        SignalEnvelope<AnswerResponse> payload = new SignalEnvelope<>(SignalType.ANSWER, response);
        sendSignal("mediaServer", payload);
    }

    public void sendICE(String senderId, String targetId, String ice) throws IOException {
        checkMediaServerConnected(targetId);
        IceResponse iceResponse = new IceResponse(targetId, ice);
        SignalEnvelope<IceResponse> payload = new SignalEnvelope<>(SignalType.ICE, iceResponse);
        if(senderId.equals(targetId)){
            sendSignal("mediaServer", payload);
            return;
        }
        sendSignal(targetId, payload);
    }

    public void sendNegotiation(String userId) throws IOException {
        checkMediaServerConnected(userId);
        NegotiationResponse negotiationResponse = new NegotiationResponse(userId);
        SignalEnvelope<NegotiationResponse> payload = new SignalEnvelope<>(SignalType.NEGOTIATION, negotiationResponse);
        sendSignal("mediaServer", payload);
    }

    public void sendLeave(String userId, String roomId) throws IOException {
        LeaveResponse response = new LeaveResponse(userId);
        TopicEnvelope<LeaveResponse> topicPayload = new TopicEnvelope<>(TopicType.LEAVE, response);
        sendTopic(userId, roomId, topicPayload);

        SignalEnvelope<LeaveResponse> payload = new SignalEnvelope<>(SignalType.LEAVE, response);
        sendSignal("mediaServer", payload);
    }

    public void sendChat(String roomId, String userId, String message) throws IOException {
        ChatResponse response = new ChatResponse(userId, message, System.currentTimeMillis());
        TopicEnvelope<ChatResponse> topicPayload = new TopicEnvelope<>(TopicType.CHAT, response);
        sendTopic(userId, roomId, topicPayload);
    }

    public void sendEmoji(String roomId, String userId, Emoji emoji) throws IOException {
        EmojiResponse response = new EmojiResponse(userId, emoji, System.currentTimeMillis());
        TopicEnvelope<EmojiResponse> topicPayload = new TopicEnvelope<>(TopicType.EMOJI, response);
        sendTopic(userId, roomId, topicPayload);
    }

    public void sendDevice(String roomId, String userId, MediaOption mediaOption) throws IOException {
        DeviceResponse response = new DeviceResponse(userId, mediaOption);
        TopicEnvelope<DeviceResponse> payload = new TopicEnvelope<>(TopicType.DEVICE, response);
        sendTopic(userId, roomId, payload);
    }

    public void sendHandUp(String roomId, String userId, boolean isHandUp) throws IOException {
        HandUpResponse response = new HandUpResponse(userId, isHandUp);
        TopicEnvelope<HandUpResponse> topicPayload = new TopicEnvelope<>(TopicType.HANDUP, response);
        sendTopic(userId, roomId, topicPayload);
    }

    public void sendError(String userId, ErrorResponse errorResponse) throws IOException {
        SignalEnvelope<ErrorResponse> payload = new SignalEnvelope<>(SignalType.ERROR, errorResponse);
        sendSignal(userId, payload);
    }

    private void sendSignal(String userId, SignalEnvelope<?> payload) throws IOException {
        signallingSessionService.sendToUser(userId, payload);
    }

    private void sendTopic(String userId, String roomId, TopicEnvelope<?> payload) throws IOException {
        signallingSessionService.sendToRoom(userId, roomId, payload);
    }

    private void checkMediaServerConnected(String senderId) throws IOException {
        if (!webSocketUserService.isUserConnected("mediaServer")) {
            ErrorResponse errorResponse = new ErrorResponse(
                    ErrorCode.E002,
                    ErrorMessage.NOT_CONNECT_MEDIA_SERVER
            );
            sendError(senderId, errorResponse);
        }
    }
}
