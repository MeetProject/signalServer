package com.meetProject.signalserver.service;

import com.meetProject.signalserver.constant.Emoji;
import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.constant.MediaType;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.model.dto.common.MediaOption;
import com.meetProject.signalserver.model.dto.common.MediaPayload;
import com.meetProject.signalserver.model.dto.socket.MediaSessionDto.CapabilitiesRequestPayload;
import com.meetProject.signalserver.model.dto.socket.MediaSessionDto.CapabilitiesResponse;
import com.meetProject.signalserver.model.dto.socket.RoomInteractionDto.*;
import com.meetProject.signalserver.model.dto.socket.RoomSessionDto.*;
import com.meetProject.signalserver.model.dto.common.User;
import com.meetProject.signalserver.model.dto.socket.ErrorResponse;
import com.meetProject.signalserver.model.dto.socket.LeaveResponse;
import com.meetProject.signalserver.model.dto.common.SignalResponse;
import com.meetProject.signalserver.model.dto.common.TopicResponse;
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

    public void sendJoin(User user, JoinPayload joinPayload, List<User> participants) {
        String userId = user.userId();

        String roomId = joinPayload.roomId();
        String correlationId = joinPayload.correlationId();
        MediaOption mediaOption = joinPayload.mediaOption();

        JoinResponse joinResponse = new JoinResponse(correlationId, participants);
        sendSignal(userId, joinResponse);

        ParticipantResponse participantResponse = new ParticipantResponse(user, mediaOption);
        sendTopic(roomId, TopicType.PARTICIPANT, participantResponse);
    }

    public void sendCapabilitiesToServer(CapabilitiesRequestPayload payload) {
        sendMedia(MediaType.CAPABILITIES, payload);
    }

    public void sendCapabilitiesToUser(CapabilitiesResponse response) {
        UserCapabilityResponse payload = new UserCapabilityResponse(response.correlationId(), response.capabilities());
        sendSignal(response.userId(), payload);
    }

    public void sendLeave(String userId, String roomId) {
        withMediaServerConnected(userId, () -> {
            LeaveResponse response = new LeaveResponse(userId, null);
            sendTopic(roomId, TopicType.LEAVE, response);
            sendSignal("mediaServer", response);
        });
    }

    public void sendChat(String roomId, String userId, String message) {
        System.out.println(userId);
        ChatResponse response = new ChatResponse(userId, message, System.currentTimeMillis());
        sendTopic(roomId, TopicType.CHAT, response);
    }

    public void sendEmoji(String roomId, String userId, Emoji emoji) {
        System.out.println(userId);
        EmojiResponse response = new EmojiResponse(userId, emoji, System.currentTimeMillis());
        sendTopic(roomId, TopicType.EMOJI, response);
    }

    public void sendDevice(String roomId, String userId, MediaOption mediaOption) {
        DeviceResponse response = new DeviceResponse(userId, mediaOption);
        sendTopic(roomId, TopicType.DEVICE, response);
    }

    public void sendHandUp(String roomId, String userId, boolean isHandUp) {
        HandUpResponse response = new HandUpResponse(userId, isHandUp);
        sendTopic(roomId, TopicType.HANDUP ,response);
    }

    public void sendError(String userId, ErrorResponse errorResponse) {
        sendSignal(userId, errorResponse);
    }

    private void sendSignal(String userId, SignalResponse payload) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/replies/" + payload.correlationId(), payload);
    }

    private void sendTopic(String roomId, TopicType type, TopicResponse payload) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/" + type.name().toLowerCase(), payload);
    }

    private void sendMedia(MediaType type, MediaPayload payload) {
        messagingTemplate.convertAndSendToUser("mediaServer","/media/" + type.name().toLowerCase(), payload);
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

    private void sendSignalConsideringSender(String senderId, String targetId, SignalResponse response) {
        if (senderId.equals("mediaServer")) {
            sendSignal(targetId, response);
            return;
        }
        sendSignal("mediaServer", response);
    }
}
