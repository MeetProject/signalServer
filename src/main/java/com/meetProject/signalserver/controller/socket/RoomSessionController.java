package com.meetProject.signalserver.controller.socket;

import com.meetProject.signalserver.constant.MediaType;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.dto.application.ParticipantPayload;
import com.meetProject.signalserver.dto.application.ResyncPayload;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.CapabilitiesRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ConsumerParamsRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.DtlsConnectRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.DtlsRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ProducerMuteRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.RtlsRequest;
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
import com.meetProject.signalserver.util.WebSocketUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class RoomSessionController {
    private final RoomService roomService;
    private final ParticipantService participantService;
    private final StompMessageSender stompMessageSender;

    public RoomSessionController(RoomService roomService, ParticipantService participantService, StompMessageSender stompMessageSender) {
        this.roomService = roomService;
        this.participantService = participantService;
        this.stompMessageSender = stompMessageSender;
    }

    @MessageMapping("/signal/capabilities")
    public void capabilities(@Payload UserCapabilityRequest capabilityPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = participantService.getJoinedRoomId(userId);

        stompMessageSender.sendToMediaServer(MediaType.CAPABILITIES, new CapabilitiesRequest(capabilityPayload.correlationId(),userId, roomId));
    }

    @MessageMapping("/signal/dtls")
    public void dtls(@Payload UserDtlsRequest dtlsPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = participantService.getJoinedRoomId(userId);

        stompMessageSender.sendToMediaServer(MediaType.DTLS, new DtlsRequest(dtlsPayload.correlationId(), userId, roomId, dtlsPayload.direction()));
    }

    @MessageMapping("/signal/dtls/connect")
    public void dtlsConnect(@Payload UserDtlsConnectRequest transportConnectPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = participantService.getJoinedRoomId(userId);

        stompMessageSender.sendToMediaServer(MediaType.DTLSCONNECT, new DtlsConnectRequest(
                transportConnectPayload.correlationId(),
                userId,
                roomId,
                transportConnectPayload.dtlsParameters(),
                transportConnectPayload.direction()
        ));
    }

    @MessageMapping("/signal/rtls")
    public void rtls(@Payload UserRtlsRequest rtlsPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = participantService.getJoinedRoomId(userId);

        stompMessageSender.sendToMediaServer(MediaType.RTLS, new RtlsRequest(rtlsPayload.correlationId(), userId, roomId, rtlsPayload.appData(), rtlsPayload.kind(), rtlsPayload.rtpParameters()));
    }

    @MessageMapping("/signal/consumerParams")
    public void consumerParams(@Payload UserConsumerParamsRequest consumerParamsPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = participantService.getJoinedRoomId(userId);

        stompMessageSender.sendToMediaServer(MediaType.PARAMS, new ConsumerParamsRequest(consumerParamsPayload.correlationId(), userId, roomId, consumerParamsPayload.targetId(),
                consumerParamsPayload.producerId(), consumerParamsPayload.rtpCapabilities()));
    }

    @MessageMapping("/signal/producer/pause")
    public void producerPause(@Payload UserProducerMuteRequest mutePayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());

        stompMessageSender.sendToMediaServer(MediaType.PRODUCER_PAUSE, new ProducerMuteRequest(mutePayload.correlationId(), userId, mutePayload.producerId()));
    }

    @MessageMapping("/signal/producer/resume")
    public void producerResume(@Payload UserProducerMuteRequest mutePayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());

        stompMessageSender.sendToMediaServer(MediaType.PRODUCER_RESUME, new ProducerMuteRequest(mutePayload.correlationId(), userId, mutePayload.producerId()));
    }

    @MessageMapping("/signal/join")
    public void join(@Payload JoinRequest joinRequest, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        ParticipantPayload participantPayload = roomService.join(userId, joinRequest);

        stompMessageSender.sendToUser(userId, new JoinResponse(joinRequest.correlationId(), participantPayload.others()));
        stompMessageSender.broadcast(joinRequest.roomId(), TopicType.PARTICIPANT, new ParticipantResponse(participantPayload.joiner()));
    }

    @MessageMapping("/signal/resync")
    public void resync(@Payload ResyncRequest resyncRequest, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        ResyncPayload resync = roomService.resync(userId);

        stompMessageSender.sendToUser(userId, new ResyncResponse(resyncRequest.correlationId(), resync.participants(), resync.rejoinRequired()));
    }
}
