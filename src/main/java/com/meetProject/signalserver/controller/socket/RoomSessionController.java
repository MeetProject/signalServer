package com.meetProject.signalserver.controller.socket;

import com.meetProject.signalserver.model.dto.socket.MediaSessionDto.CapabilitiesRequestPayload;
import com.meetProject.signalserver.model.dto.socket.MediaSessionDto.DtlsRequestPayload;
import com.meetProject.signalserver.model.dto.socket.RoomSessionDto.JoinPayload;
import com.meetProject.signalserver.model.dto.socket.RoomSessionDto.UserCapabilityPayload;
import com.meetProject.signalserver.model.dto.socket.RoomSessionDto.UserDtlsPayload;
import com.meetProject.signalserver.orchestration.JoinOrchestrationService;
import com.meetProject.signalserver.orchestration.LeaveOrchestrationService;
import com.meetProject.signalserver.service.UserService;
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
    private final UserService userService;

    public RoomSessionController(MediaMessagingService mediaMessagingService,  LeaveOrchestrationService leaveService,  JoinOrchestrationService joinService, UserService userService) {
        this.mediaMessagingService = mediaMessagingService;
        this.joinService = joinService;
        this.leaveService = leaveService;
        this.userService = userService;
    }

    @MessageMapping("/signal/capabilities")
    public void capabilities(@Payload UserCapabilityPayload capabilityPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        CapabilitiesRequestPayload payload = new CapabilitiesRequestPayload(capabilityPayload.correlationId(),userId);
        mediaMessagingService.sendCapabilities(payload);
    }

    @MessageMapping("/signal/dtls")
    public void dtls(@Payload UserDtlsPayload dtlsPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        DtlsRequestPayload payload = new DtlsRequestPayload(dtlsPayload.correlationId(), userId, dtlsPayload.direction());
        mediaMessagingService.sendDtls(payload);
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
