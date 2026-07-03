package com.meetProject.signalserver.infrastructure;

import com.meetProject.signalserver.constant.MediaType;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.dto.common.MediaPayload;
import com.meetProject.signalserver.dto.common.SignalResponse;
import com.meetProject.signalserver.dto.common.TopicResponse;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class StompMessageSender {
    private final SimpMessagingTemplate messagingTemplate;

    public StompMessageSender(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendToUser(String userId, SignalResponse payload) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/replies", payload);
    }

    public void broadcast(String roomId, TopicType type, TopicResponse payload) {
        messagingTemplate.convertAndSend("/topic/room/" + roomId + "/" + type.path(), payload);
    }

    public void sendToMediaServer(MediaType type, MediaPayload payload) {
        messagingTemplate.convertAndSendToUser("mediaServer", "/media/" + type.path(), payload);
    }
}
