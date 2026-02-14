package com.meetProject.signalserver.controller.socket;

import com.meetProject.signalserver.model.dto.socket.MediaSessionDto.CapabilitiesResponse;
import com.meetProject.signalserver.service.SignalMessagingService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;

public class MediaSessionController {
    private final SignalMessagingService signalMessagingService;

    public MediaSessionController(SignalMessagingService signalMessagingService) {
        this.signalMessagingService = signalMessagingService;
    }

    @MessageMapping("/media/capabilities")
    public void capabilities(@Payload CapabilitiesResponse capabilitiesResponse) {
        signalMessagingService.sendCapabilitiesToUser(capabilitiesResponse);
    }
}
