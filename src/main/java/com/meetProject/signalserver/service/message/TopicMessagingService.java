package com.meetProject.signalserver.service.message;

import com.meetProject.signalserver.constant.Emoji;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.model.dto.common.MediaOption;
import com.meetProject.signalserver.model.dto.common.Participant;
import com.meetProject.signalserver.model.dto.common.TopicResponse;
import com.meetProject.signalserver.model.dto.socket.LeaveResponse;
import com.meetProject.signalserver.model.dto.socket.MediaSessionDto.RtlsResponse;
import com.meetProject.signalserver.model.dto.socket.RoomInteractionDto.ChatResponse;
import com.meetProject.signalserver.model.dto.socket.RoomInteractionDto.DeviceResponse;
import com.meetProject.signalserver.model.dto.socket.RoomInteractionDto.EmojiResponse;
import com.meetProject.signalserver.model.dto.socket.RoomInteractionDto.HandUpResponse;
import com.meetProject.signalserver.model.dto.socket.RoomInteractionDto.ParticipantResponse;
import com.meetProject.signalserver.model.dto.socket.RoomInteractionDto.ProducerResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class TopicMessagingService {
    private final SimpMessagingTemplate messagingTemplate;

    public TopicMessagingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendProducerId(RtlsResponse rtlsResponse) {
        ProducerResponse response = new ProducerResponse(rtlsResponse.producerId());
        sendTopic(rtlsResponse.roomId(), TopicType.RTLS, response);
    }

    public void sendJoin(String roomId, Participant participant) {
        ParticipantResponse participantResponse = new ParticipantResponse(participant);
        sendTopic(roomId, TopicType.PARTICIPANT, participantResponse);
    }


    public void sendLeave(String userId, String roomId) {
        LeaveResponse response = new LeaveResponse(userId, null);
        sendTopic(roomId, TopicType.LEAVE, response);
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
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/" + type.name().toLowerCase(), payload);
    }
}
