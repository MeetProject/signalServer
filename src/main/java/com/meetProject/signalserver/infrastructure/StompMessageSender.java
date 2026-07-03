package com.meetProject.signalserver.infrastructure;

import com.meetProject.signalserver.config.MediaServerProperties;
import com.meetProject.signalserver.constant.MediaType;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.dto.socket.MediaRequest;
import com.meetProject.signalserver.dto.socket.SignalResponse;
import com.meetProject.signalserver.dto.socket.TopicResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class StompMessageSender {
    private final SimpMessagingTemplate messagingTemplate;
    private final MediaServerProperties mediaServerProperties;

    public StompMessageSender(SimpMessagingTemplate messagingTemplate, MediaServerProperties mediaServerProperties) {
        this.messagingTemplate = messagingTemplate;
        this.mediaServerProperties = mediaServerProperties;
    }

    public void sendToUser(String userId, SignalResponse payload) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/replies", payload);
    }

    public void broadcast(String roomId, TopicType type, TopicResponse payload) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/" + type.path(), payload);
    }

    public void sendToMediaServer(MediaType type, MediaRequest payload) {
        messagingTemplate.convertAndSendToUser(mediaServerProperties.id(), "/media/" + type.path(), payload);
    }
}
