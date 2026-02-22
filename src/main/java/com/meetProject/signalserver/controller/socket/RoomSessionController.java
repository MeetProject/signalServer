package com.meetProject.signalserver.controller.socket;

import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.model.dto.socket.MediaSessionDto.*;
import com.meetProject.signalserver.model.dto.socket.RoomSessionDto.*;
import com.meetProject.signalserver.orchestration.JoinOrchestrationService;
import com.meetProject.signalserver.orchestration.LeaveOrchestrationService;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.message.MediaMessagingService;
import com.meetProject.signalserver.util.WebSocketUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class RoomSessionController {
    private final MediaMessagingService mediaMessagingService;
    private final JoinOrchestrationService joinService;
    private final LeaveOrchestrationService leaveService;
    private final RoomsService roomsService;

    public RoomSessionController(MediaMessagingService mediaMessagingService,  LeaveOrchestrationService leaveService,  JoinOrchestrationService joinService, RoomsService roomsService) {
        this.mediaMessagingService = mediaMessagingService;
        this.joinService = joinService;
        this.leaveService = leaveService;
        this.roomsService = roomsService;
    }

    @MessageMapping("/signal/capabilities")
    public void capabilities(@Payload UserCapabilityPayload capabilityPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = roomsService.getRoomId(userId);

        System.out.println("get capabilities " + userId + " roomId: " + roomId);

        if(roomId == null) {
            throw new IllegalArgumentException(ErrorMessage.USER_NOT_JOINED);
        }

        CapabilitiesRequestPayload payload = new CapabilitiesRequestPayload(capabilityPayload.correlationId(),userId, roomId);
        mediaMessagingService.sendCapabilities(payload);
    }

    @MessageMapping("/signal/dtls")
    public void dtls(@Payload UserDtlsPayload dtlsPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = roomsService.getRoomId(userId);

        if(roomId == null) {
            throw new IllegalArgumentException(ErrorMessage.USER_NOT_JOINED);
        }

        DtlsRequestPayload payload = new DtlsRequestPayload(dtlsPayload.correlationId(), userId, roomId, dtlsPayload.direction());
        mediaMessagingService.sendDtls(payload);
    }

    @MessageMapping("/signal/dtls/connect")
    public void dtlsConnect(@Payload UserDtlsConnectPayload transportConnectPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = roomsService.getRoomId(userId);

        if(roomId == null) {
            throw new IllegalArgumentException(ErrorMessage.USER_NOT_JOINED);
        }

        DtlsConnectPayload payload = new DtlsConnectPayload(transportConnectPayload.correlationId(), userId, roomId, transportConnectPayload.dtlsParameters(), transportConnectPayload.direction());
        mediaMessagingService.sendDtlsConnect(payload);
    }

    @MessageMapping("/signal/rtls")
    public void rtls(@Payload UserRtlsPayload rtlsPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());

        RtlsRequestPayload payload = new RtlsRequestPayload(rtlsPayload.correlationId(), userId, rtlsPayload.appData(), rtlsPayload.kind(), rtlsPayload.rtpParameters());
        mediaMessagingService.sendRtls(payload);
    }

    @MessageMapping("/signal/consumerParams")
    public void consumerParams(@Payload UserConsumerParamsPayload consumerParamsPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = roomsService.getRoomId(userId);

        if(roomId == null) {
            throw new IllegalArgumentException(ErrorMessage.USER_NOT_JOINED);
        }

        ConsumerParamsRequestPayload payload = new ConsumerParamsRequestPayload(consumerParamsPayload.correlationId(), userId, roomId, consumerParamsPayload.targetId(),
                consumerParamsPayload.producerId(), consumerParamsPayload.rtpCapabilities());
        mediaMessagingService.sendConsumerParams(payload);
    }

    @MessageMapping("/signal/resume")
    public void resume(@Payload UserResumePayload resumePayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());

        ResumeRequestPayload payload = new ResumeRequestPayload(resumePayload.correlationId(), userId, resumePayload.consumerId());
        mediaMessagingService.sendResume(payload);
    }

    @MessageMapping("/signal/join")
    public void join(@Payload JoinPayload joinPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        joinService.joinRoom(userId, joinPayload);
    }


    @MessageMapping("/signal/leave")
    public void leave(SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        leaveService.leaveUser(userId);
    }

}
