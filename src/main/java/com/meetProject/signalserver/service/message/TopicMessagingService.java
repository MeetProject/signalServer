package com.meetProject.signalserver.service.message;

import com.meetProject.signalserver.constant.Emoji;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.domain.MediaOption;
import com.meetProject.signalserver.domain.Participant;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.RtlsResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.ChatResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.DeviceResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.EmojiResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.HandUpResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.LeaveResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.ParticipantResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.ProducerResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.RemoveProducerResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import com.meetProject.signalserver.dto.common.TopicResponse;
import org.springframework.stereotype.Service;

@Service
public class TopicMessagingService {
    private final SimpMessagingTemplate messagingTemplate;

    public TopicMessagingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendProducerId(String roomId, RtlsResponse rtlsResponse) {
        ProducerResponse response = new ProducerResponse(rtlsResponse.userId(), rtlsResponse.producerId());
        sendTopic(roomId, TopicType.RTLS, response);
    }

    public void sendRemoveProducerId(String roomId, String userId, String trackType) {
        RemoveProducerResponse response = new RemoveProducerResponse(userId, trackType);
        sendTopic(roomId, TopicType.PRODUCER_REMOVE, response);
    }

    public void sendJoin(String roomId, Participant participant) {
        ParticipantResponse participantResponse = new ParticipantResponse(participant);
        sendTopic(roomId, TopicType.PARTICIPANT, participantResponse);
    }


    public void sendLeave(String userId, String roomId) {
        LeaveResponse leaveResponse = new LeaveResponse(userId);
        sendTopic(roomId, TopicType.LEAVE, leaveResponse);
    }

    public void sendChat(String roomId, String userId, String message) {
        ChatResponse response = new ChatResponse(userId, message, System.currentTimeMillis());
        sendTopic(roomId, TopicType.CHAT, response);
    }

    public void sendEmoji(String roomId, String userId, Emoji emoji) {
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

    private void sendTopic(String roomId, TopicType type, TopicResponse payload) {
        String path = String.join("/",type.name().toLowerCase().split("_"));
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/" + path, payload);
    }
}
