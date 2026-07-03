package com.meetProject.signalserver.controller.socket;

import com.meetProject.signalserver.constant.MediaType;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.domain.Participant;
import com.meetProject.signalserver.dto.application.JoinResult;
import com.meetProject.signalserver.dto.application.ResyncResult;
import com.meetProject.signalserver.dto.socket.ParticipantDto;
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
import java.util.List;
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
    public void capabilities(@Payload UserCapabilityRequest request, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = participantService.getJoinedRoomId(userId);

        stompMessageSender.sendToMediaServer(MediaType.CAPABILITIES, request.toMediaRequest(userId, roomId));
    }

    @MessageMapping("/signal/dtls")
    public void dtls(@Payload UserDtlsRequest request, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = participantService.getJoinedRoomId(userId);

        stompMessageSender.sendToMediaServer(MediaType.DTLS, request.toMediaRequest(userId, roomId));
    }

    @MessageMapping("/signal/dtls/connect")
    public void dtlsConnect(@Payload UserDtlsConnectRequest request, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = participantService.getJoinedRoomId(userId);

        stompMessageSender.sendToMediaServer(MediaType.DTLSCONNECT, request.toMediaRequest(userId, roomId));
    }

    @MessageMapping("/signal/rtls")
    public void rtls(@Payload UserRtlsRequest request, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = participantService.getJoinedRoomId(userId);

        stompMessageSender.sendToMediaServer(MediaType.RTLS, request.toMediaRequest(userId, roomId));
    }

    @MessageMapping("/signal/consumerParams")
    public void consumerParams(@Payload UserConsumerParamsRequest request, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = participantService.getJoinedRoomId(userId);

        stompMessageSender.sendToMediaServer(MediaType.PARAMS, request.toMediaRequest(userId, roomId));
    }

    @MessageMapping("/signal/producer/pause")
    public void producerPause(@Payload UserProducerMuteRequest request, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());

        stompMessageSender.sendToMediaServer(MediaType.PRODUCER_PAUSE, request.toMediaRequest(userId));
    }

    @MessageMapping("/signal/producer/resume")
    public void producerResume(@Payload UserProducerMuteRequest request, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());

        stompMessageSender.sendToMediaServer(MediaType.PRODUCER_RESUME, request.toMediaRequest(userId));
    }

    @MessageMapping("/signal/join")
    public void join(@Payload JoinRequest joinRequest, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        JoinResult result = roomService.join(userId, joinRequest.roomId(), joinRequest.mediaOption());

        stompMessageSender.sendToUser(userId, new JoinResponse(joinRequest.correlationId(), toDto(result.others())));
        stompMessageSender.broadcast(joinRequest.roomId(), TopicType.PARTICIPANT, new ParticipantResponse(ParticipantDto.from(result.joiner())));
    }

    @MessageMapping("/signal/resync")
    public void resync(@Payload ResyncRequest resyncRequest, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        ResyncResult result = roomService.resync(userId);

        stompMessageSender.sendToUser(userId, new ResyncResponse(resyncRequest.correlationId(), toDto(result.participants()), result.rejoinRequired()));
    }

    private List<ParticipantDto> toDto(List<Participant> participants) {
        return participants.stream()
                .map(ParticipantDto::from)
                .toList();
    }
}
