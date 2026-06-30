package com.meetProject.signalserver.service.message;

import com.meetProject.signalserver.constant.MediaType;
import com.meetProject.signalserver.dto.common.MediaPayload;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.CapabilitiesRequestPayload;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ConsumerParamsRequestPayload;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ConsumerPauseRequestPayload;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ConsumerResumeRequestPayload;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.DtlsConnectPayload;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.DtlsRequestPayload;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.MediaLeavePayload;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ProducerMutePayload;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ProducerRemovePayload;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.RtlsRequestPayload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MediaMessagingService {
    private final SimpMessagingTemplate messagingTemplate;

    public MediaMessagingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendCapabilities(CapabilitiesRequestPayload payload) {
        sendMedia(MediaType.CAPABILITIES, payload);
    }

    public void sendDtls(DtlsRequestPayload payload) {
        sendMedia(MediaType.DTLS, payload);
    }

    public void sendDtlsConnect(DtlsConnectPayload payload) {
        sendMedia(MediaType.DTLSCONNECT, payload);
    }

    public void sendRtls(RtlsRequestPayload payload) {
        sendMedia(MediaType.RTLS, payload);
    }

    public void sendConsumerParams(ConsumerParamsRequestPayload payload) {
        sendMedia(MediaType.PARAMS, payload);
    }

    public void sendConsumerResume(ConsumerResumeRequestPayload payload) {
        sendMedia(MediaType.CONSUMER_RESUME, payload);
    }

    public void sendConsumerPause(ConsumerPauseRequestPayload payload) {
        sendMedia(MediaType.CONSUMER_PAUSE, payload);
    }

    public void sendProducerPause(ProducerMutePayload payload) {
        sendMedia(MediaType.PRODUCER_PAUSE, payload);
    }

    public void sendProducerResume(ProducerMutePayload payload) {
        sendMedia(MediaType.PRODUCER_RESUME, payload);
    }

    public void sendProducerRemove(ProducerRemovePayload payload) {
        sendMedia(MediaType.PRODUCER_REMOVE, payload);
    }

    public void sendLeave(MediaLeavePayload payload) {
        sendMedia(MediaType.LEAVE, payload);
    }

    private void sendMedia(MediaType type, MediaPayload payload) {
        String path = String.join("/",type.name().toLowerCase().split("_"));
        messagingTemplate.convertAndSendToUser("mediaServer","/media/" + path, payload);
    }
}
