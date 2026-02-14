package com.meetProject.signalserver.service.message;

import com.meetProject.signalserver.constant.MediaType;
import com.meetProject.signalserver.model.dto.common.MediaPayload;
import com.meetProject.signalserver.model.dto.socket.MediaSessionDto.CapabilitiesRequestPayload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MediaMessagingService {
    private final SimpMessagingTemplate messagingTemplate;

    public MediaMessagingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendCapabilitiesToServer(CapabilitiesRequestPayload payload) {
        sendMedia(MediaType.CAPABILITIES, payload);
    }

    private void sendMedia(MediaType type, MediaPayload payload) {
        messagingTemplate.convertAndSendToUser("mediaServer","/media/" + type.name().toLowerCase(), payload);
    }
}
