package com.meetProject.signalserver.controller.socket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.meetProject.signalserver.constant.DtlsDirection;
import com.meetProject.signalserver.constant.MediaType;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.domain.MediaOption;
import com.meetProject.signalserver.domain.Participant;
import com.meetProject.signalserver.domain.User;
import com.meetProject.signalserver.dto.application.JoinResult;
import com.meetProject.signalserver.dto.application.ResyncResult;
import com.meetProject.signalserver.dto.common.AppData;
import com.meetProject.signalserver.dto.common.RtpCapabilities;
import com.meetProject.signalserver.dto.common.RtpParameters;
import com.meetProject.signalserver.dto.common.TransportOptions.DtlsParameters;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.CapabilitiesRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ConsumerParamsRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.DtlsConnectRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.DtlsRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.MediaLeaveRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ProducerMuteRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.RtlsRequest;
import com.meetProject.signalserver.dto.socket.ParticipantDto;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.LeaveResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.ParticipantResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.JoinRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.JoinResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.ResyncRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.ResyncResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserCapabilityRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserConsumerParamsRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserDtlsConnectRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserDtlsRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserProducerMuteRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserRtlsRequest;
import com.meetProject.signalserver.infrastructure.StompMessageSender;
import com.meetProject.signalserver.service.ParticipantService;
import com.meetProject.signalserver.service.RoomService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class RoomSessionControllerTest {
    @Mock RoomService roomService;
    @Mock ParticipantService participantService;
    @Mock StompMessageSender sender;
    @InjectMocks RoomSessionController controller;

    private final Principal principal = () -> "u1";
    private final MediaOption mediaOption = new MediaOption(true, true);

    private Participant participant(String userId) {
        return Participant.join(User.restore(userId, "name", "#ffffff", LocalDateTime.now()), mediaOption);
    }

    @Test
    @DisplayName("capabilities 요청을 미디어서버로 릴레이한다")
    void relaysCapabilities() {
        given(participantService.getJoinedRoomId("u1")).willReturn("roomA");

        controller.capabilities(new UserCapabilityRequest("cid"), principal);

        verify(sender).sendToMediaServer(MediaType.CAPABILITIES, new CapabilitiesRequest("cid", "u1", "roomA"));
    }

    @Test
    @DisplayName("dtls 요청을 미디어서버로 릴레이한다")
    void relaysDtls() {
        given(participantService.getJoinedRoomId("u1")).willReturn("roomA");

        controller.dtls(new UserDtlsRequest("cid", DtlsDirection.send), principal);

        verify(sender).sendToMediaServer(MediaType.DTLS, new DtlsRequest("cid", "u1", "roomA", DtlsDirection.send));
    }

    @Test
    @DisplayName("dtls/connect 요청을 미디어서버로 릴레이한다")
    void relaysDtlsConnect() {
        given(participantService.getJoinedRoomId("u1")).willReturn("roomA");
        DtlsParameters params = new DtlsParameters("client", List.of());

        controller.dtlsConnect(new UserDtlsConnectRequest("cid", params, "send"), principal);

        verify(sender).sendToMediaServer(MediaType.DTLSCONNECT,
                new DtlsConnectRequest("cid", "u1", "roomA", params, "send"));
    }

    @Test
    @DisplayName("rtls 요청을 미디어서버로 릴레이한다")
    void relaysRtls() {
        given(participantService.getJoinedRoomId("u1")).willReturn("roomA");
        AppData appData = new AppData("audio", "u1");
        RtpParameters rtpParameters = new RtpParameters(null, List.of(), List.of(), List.of(), null);

        controller.rtls(new UserRtlsRequest("cid", appData, "audio", rtpParameters), principal);

        verify(sender).sendToMediaServer(MediaType.RTLS,
                new RtlsRequest("cid", "u1", "roomA", appData, "audio", rtpParameters));
    }

    @Test
    @DisplayName("consumerParams 요청을 미디어서버로 릴레이한다")
    void relaysConsumerParams() {
        given(participantService.getJoinedRoomId("u1")).willReturn("roomA");
        RtpCapabilities caps = new RtpCapabilities(List.of(), List.of());

        controller.consumerParams(new UserConsumerParamsRequest("cid", "t1", "p1", caps), principal);

        verify(sender).sendToMediaServer(MediaType.PARAMS,
                new ConsumerParamsRequest("cid", "u1", "roomA", "t1", "p1", caps));
    }

    @Test
    @DisplayName("producer pause 요청을 미디어서버로 릴레이한다")
    void relaysProducerPause() {
        controller.producerPause(new UserProducerMuteRequest("cid", "p1"), principal);

        verify(sender).sendToMediaServer(MediaType.PRODUCER_PAUSE, new ProducerMuteRequest("cid", "u1", "p1"));
    }

    @Test
    @DisplayName("producer resume 요청을 미디어서버로 릴레이한다")
    void relaysProducerResume() {
        controller.producerResume(new UserProducerMuteRequest("cid", "p1"), principal);

        verify(sender).sendToMediaServer(MediaType.PRODUCER_RESUME, new ProducerMuteRequest("cid", "u1", "p1"));
    }

    @Test
    @DisplayName("join은 요청자에게 기존참여자를, 방에는 신규참여자를 보낸다")
    void joinSendsRepliesAndBroadcast() {
        Participant joiner = participant("u1");
        Participant other = participant("u2");
        given(roomService.leave("u1")).willReturn(Optional.empty());
        given(roomService.join("u1", "roomA", mediaOption))
                .willReturn(new JoinResult(joiner, List.of(other)));

        controller.join(new JoinRequest("roomA", "cid", mediaOption), principal);

        verify(sender).sendToUser("u1", new JoinResponse("cid", List.of(ParticipantDto.from(other))));
        verify(sender).broadcast("roomA", TopicType.PARTICIPANT,
                new ParticipantResponse(ParticipantDto.from(joiner)));
    }

    @Test
    @DisplayName("다른 방에 있던 사용자가 join하면 기존 방에 퇴장 알림을 보낸다")
    void joinLeavesPreviousRoom() {
        Participant joiner = participant("u1");
        given(roomService.leave("u1")).willReturn(Optional.of("roomOld"));
        given(roomService.join("u1", "roomA", mediaOption))
                .willReturn(new JoinResult(joiner, List.of()));

        controller.join(new JoinRequest("roomA", "cid", mediaOption), principal);

        verify(sender).broadcast("roomOld", TopicType.LEAVE, new LeaveResponse("u1"));
        verify(sender).sendToMediaServer(MediaType.LEAVE, new MediaLeaveRequest("roomOld", "u1"));
        verify(sender).broadcast("roomA", TopicType.PARTICIPANT,
                new ParticipantResponse(ParticipantDto.from(joiner)));
    }

    @Test
    @DisplayName("resync는 참여자 목록과 재입장 필요여부를 요청자에게 보낸다")
    void resyncSendsReply() {
        Participant p = participant("u1");
        given(roomService.resync("u1", "roomA")).willReturn(new ResyncResult(List.of(p), false));

        controller.resync(new ResyncRequest("roomA", "cid"), principal);

        verify(sender).sendToUser("u1",
                new ResyncResponse("cid", List.of(ParticipantDto.from(p)), false));
    }

    @Test
    @DisplayName("resync 시 세션이 없으면 재입장 필요로 보고된다")
    void resyncRequiresRejoin() {
        given(roomService.resync("u1", "roomA")).willReturn(new ResyncResult(List.of(), true));

        controller.resync(new ResyncRequest("roomA", "cid"), principal);

        verify(sender).sendToUser("u1", new ResyncResponse("cid", List.of(), true));
    }
}
