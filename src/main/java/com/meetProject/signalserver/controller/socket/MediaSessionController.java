package com.meetProject.signalserver.controller.socket;

import com.meetProject.signalserver.model.dto.socket.MediaSessionDto.*;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.message.SignalMessagingService;
import com.meetProject.signalserver.service.message.TopicMessagingService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class MediaSessionController {
    private final SignalMessagingService signalMessagingService;
    private final TopicMessagingService topicMessagingService;
    private final RoomsService roomsService;

    public MediaSessionController(SignalMessagingService signalMessagingService, TopicMessagingService topicMessagingService, RoomsService roomsService) {
        this.signalMessagingService = signalMessagingService;
        this.topicMessagingService = topicMessagingService;
        this.roomsService = roomsService;
    }

    @MessageMapping("/media/capabilities")
    public void capabilities(@Payload CapabilitiesResponse capabilitiesResponse) {
        signalMessagingService.sendCapabilitiesToUser(capabilitiesResponse);
    }

    @MessageMapping("/media/dtls")
    public void dtls(@Payload DtlsResponse dtlsResponse) {
        signalMessagingService.sendDtls(dtlsResponse);
    }

    @MessageMapping("/media/dtls/connect")
    public void dtlsConnect(@Payload DtlsConnectResponse dtlsConnectResponse) {
        signalMessagingService.sendDtlsConnect(dtlsConnectResponse);
    }

    @MessageMapping("/media/rtls")
    public void rtls(@Payload RtlsResponse rtlsResponse) {
        String roomId = roomsService.getRoomId(rtlsResponse.userId());
        roomsService.addProducer(roomId, rtlsResponse.userId(), rtlsResponse.producerId());
        signalMessagingService.sendRtls(rtlsResponse);
        topicMessagingService.sendProducerId(roomId, rtlsResponse);
    }

    @MessageMapping("/media/consumerParams")
    public void consumerParams(@Payload ConsumerParamsResponse consumerParamsResponse) {
        signalMessagingService.sendConsumerParams(consumerParamsResponse);
    }

    @MessageMapping("/media/resume")
    public void resume(@Payload ResumeResponse resumeResponse) {
        signalMessagingService.sendResume(resumeResponse);
    }
}
