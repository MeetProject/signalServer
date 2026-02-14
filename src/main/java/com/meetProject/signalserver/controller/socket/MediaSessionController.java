package com.meetProject.signalserver.controller.socket;

import com.meetProject.signalserver.model.dto.socket.MediaSessionDto.CapabilitiesResponse;
import com.meetProject.signalserver.model.dto.socket.MediaSessionDto.DtlsResponse;
import com.meetProject.signalserver.service.message.SignalMessagingService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class MediaSessionController {
    private final SignalMessagingService signalMessagingService;

    public MediaSessionController(SignalMessagingService signalMessagingService) {
        this.signalMessagingService = signalMessagingService;
    }

    @MessageMapping("/media/capabilities")
    public void capabilities(@Payload CapabilitiesResponse capabilitiesResponse) {
        signalMessagingService.sendCapabilitiesToUser(capabilitiesResponse);
    }

    @MessageMapping("/media/dtls")
    public void dtls(@Payload DtlsResponse dtlsResponse) {
        signalMessagingService.sendDtls(dtlsResponse);
    }
}
