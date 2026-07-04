package com.meetProject.signalserver.controller.socket;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.meetProject.signalserver.constant.ErrorCode;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.dto.application.ProducerResult;
import com.meetProject.signalserver.dto.common.ConsumerParams;
import com.meetProject.signalserver.dto.common.RtpCapabilities;
import com.meetProject.signalserver.dto.common.TransportOptions;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.CapabilitiesResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ConsumerParamsResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.DtlsConnectResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.DtlsResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.MediaErrorResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ProducerMuteResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.RtlsResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.ProducerResponse;
import com.meetProject.signalserver.dto.socket.SignalErrorResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserCapabilityResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserConsumerParamsResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserDtlsConnectResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserDtlsResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserProducerMuteResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserRtlsResponse;
import com.meetProject.signalserver.infrastructure.StompMessageSender;
import com.meetProject.signalserver.service.ParticipantService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MediaSessionControllerTest {
    @Mock StompMessageSender sender;
    @Mock ParticipantService participantService;
    @InjectMocks MediaSessionController controller;

    @Test
    @DisplayName("capabilities 응답을 대상 사용자에게 전달한다")
    void forwardsCapabilities() {
        RtpCapabilities caps = new RtpCapabilities(java.util.List.of(), java.util.List.of());

        controller.capabilities(new CapabilitiesResponse("cid", "u1", caps));

        verify(sender).sendToUser("u1", new UserCapabilityResponse("cid", caps));
    }

    @Test
    @DisplayName("dtls 응답을 대상 사용자에게 전달한다")
    void forwardsDtls() {
        TransportOptions options = new TransportOptions(null, null, null, null, null, null, null, null, null, null);

        controller.dtls(new DtlsResponse("cid", "u1", options));

        verify(sender).sendToUser("u1", new UserDtlsResponse("cid", options));
    }

    @Test
    @DisplayName("dtls/connect 응답을 대상 사용자에게 전달한다")
    void forwardsDtlsConnect() {
        controller.dtlsConnect(new DtlsConnectResponse("cid", "u1"));

        verify(sender).sendToUser("u1", new UserDtlsConnectResponse("cid"));
    }

    @Test
    @DisplayName("rtls 응답은 프로듀서 등록 후 요청자 전달과 방 브로드캐스트를 한다")
    void registersProducerAndBroadcasts() {
        given(participantService.registerProducer("u1", "p1"))
                .willReturn(new ProducerResult("roomA", "u1", "p1"));

        controller.rtls(new RtlsResponse("cid", "u1", "p1"));

        verify(sender).sendToUser("u1", new UserRtlsResponse("cid", "p1"));
        verify(sender).broadcast("roomA", TopicType.RTLS, new ProducerResponse("u1", "p1"));
    }

    @Test
    @DisplayName("consumerParams 응답을 대상 사용자에게 전달한다")
    void forwardsConsumerParams() {
        ConsumerParams cp = new ConsumerParams("id", "p1", "audio", null, null);

        controller.consumerParams(new ConsumerParamsResponse("cid", "u1", cp));

        verify(sender).sendToUser("u1", new UserConsumerParamsResponse("cid", cp));
    }

    @Test
    @DisplayName("producer mute 응답을 대상 사용자에게 전달한다")
    void forwardsProducerMute() {
        controller.producerMute(new ProducerMuteResponse("cid", "u1"));

        verify(sender).sendToUser("u1", new UserProducerMuteResponse("cid"));
    }

    @Test
    @DisplayName("미디어 서버 에러를 대상 사용자에게 에러 응답으로 전달한다")
    void forwardsMediaError() {
        controller.error(new MediaErrorResponse("cid", "u1"));

        verify(sender).sendToUser("u1",
                new SignalErrorResponse("cid", ErrorCode.INTERNAL_ERROR, "미디어 서버 처리 중 오류가 발생했습니다."));
    }
}
