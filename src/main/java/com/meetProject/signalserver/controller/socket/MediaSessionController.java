package com.meetProject.signalserver.controller.socket;

import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.CapabilitiesResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ConsumerParamsResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.DtlsConnectResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.DtlsResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.MediaErrorResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ProducerMuteResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.RtlsResponse;
import com.meetProject.signalserver.dto.socket.SignalErrorResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.ProducerResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserCapabilityResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserConsumerParamsResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserDtlsConnectResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserDtlsResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserProducerMuteResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserRtlsResponse;
import com.meetProject.signalserver.infrastructure.StompMessageSender;
import com.meetProject.signalserver.service.ParticipantService;
import com.meetProject.signalserver.dto.application.ProducerResult;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class MediaSessionController {
    private final StompMessageSender stompMessageSender;
    private final ParticipantService participantService;

    public MediaSessionController(StompMessageSender stompMessageSender, ParticipantService participantService) {
        this.stompMessageSender = stompMessageSender;
        this.participantService = participantService;
    }

    @MessageMapping("/media/capabilities")
    public void capabilities(@Payload CapabilitiesResponse capabilitiesResponse) {
        UserCapabilityResponse response = new UserCapabilityResponse(capabilitiesResponse.correlationId(), capabilitiesResponse.capabilities());
        stompMessageSender.sendToUser(capabilitiesResponse.userId(), response);
    }

    @MessageMapping("/media/dtls")
    public void dtls(@Payload DtlsResponse dtlsResponse) {
        UserDtlsResponse response = new UserDtlsResponse(dtlsResponse.correlationId(), dtlsResponse.options());
        stompMessageSender.sendToUser(dtlsResponse.userId(), response);
    }

    @MessageMapping("/media/dtls/connect")
    public void dtlsConnect(@Payload DtlsConnectResponse dtlsConnectResponse) {
        UserDtlsConnectResponse response = new UserDtlsConnectResponse(dtlsConnectResponse.correlationId());
        stompMessageSender.sendToUser(dtlsConnectResponse.userId(), response);
    }

    @MessageMapping("/media/rtls")
    public void rtls(@Payload RtlsResponse rtlsResponse) {
        ProducerResult registration = participantService.registerProducer(rtlsResponse.userId(), rtlsResponse.producerId());

        UserRtlsResponse response = new UserRtlsResponse(rtlsResponse.correlationId(), rtlsResponse.producerId());
        stompMessageSender.sendToUser(rtlsResponse.userId(), response);

        ProducerResponse producerResponse = new ProducerResponse(rtlsResponse.userId(), rtlsResponse.producerId());
        stompMessageSender.broadcast(registration.roomId(), TopicType.RTLS, producerResponse);
    }

    @MessageMapping("/media/consumerParams")
    public void consumerParams(@Payload ConsumerParamsResponse consumerParamsResponse) {
        UserConsumerParamsResponse response = new UserConsumerParamsResponse(consumerParamsResponse.correlationId(), consumerParamsResponse.consumerParams());
        stompMessageSender.sendToUser(consumerParamsResponse.userId(), response);
    }

    @MessageMapping({"/media/producer/pause", "/media/producer/resume"})
    public void producerMute(@Payload ProducerMuteResponse muteResponse) {
        UserProducerMuteResponse response = new UserProducerMuteResponse(muteResponse.correlationId());
        stompMessageSender.sendToUser(muteResponse.userId(), response);
    }

    @MessageMapping("/media/error")
    public void error(@Payload MediaErrorResponse errorResponse) {
        SignalErrorResponse response = new SignalErrorResponse(
                errorResponse.correlationId(), ErrorCode.INTERNAL_ERROR, "미디어 서버 처리 중 오류가 발생했습니다.");
        stompMessageSender.sendToUser(errorResponse.userId(), response);
    }
}
