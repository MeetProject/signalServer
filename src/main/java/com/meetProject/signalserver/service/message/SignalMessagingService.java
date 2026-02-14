package com.meetProject.signalserver.service.message;

import com.meetProject.signalserver.model.dto.socket.MediaSessionDto.CapabilitiesResponse;
import com.meetProject.signalserver.model.dto.socket.MediaSessionDto.DtlsConnectResponse;
import com.meetProject.signalserver.model.dto.socket.MediaSessionDto.DtlsResponse;
import com.meetProject.signalserver.model.dto.socket.RoomInteractionDto.*;
import com.meetProject.signalserver.model.dto.socket.RoomSessionDto.*;
import com.meetProject.signalserver.model.dto.common.User;
import com.meetProject.signalserver.model.dto.socket.ErrorResponse;
import com.meetProject.signalserver.model.dto.common.SignalResponse;
import java.util.List;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SignalMessagingService {
    private final SimpMessagingTemplate messagingTemplate;

    public SignalMessagingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendJoin(User user, JoinPayload joinPayload, List<User> participants) {
        String userId = user.userId();

        String correlationId = joinPayload.correlationId();

        JoinResponse joinResponse = new JoinResponse(correlationId, participants);
        sendSignal(userId, joinResponse);
    }

    public void sendCapabilitiesToUser(CapabilitiesResponse response) {
        UserCapabilityResponse payload = new UserCapabilityResponse(response.correlationId(), response.capabilities());
        sendSignal(response.userId(), payload);
    }

    public void sendDtls(DtlsResponse response) {
        UserDtlsResponse payload = new UserDtlsResponse(response.correlationId(), response.options(), response.direction());
        sendSignal(response.userId(), payload);
    }

    public void sendDtlsConnect(DtlsConnectResponse response) {
        UserDtlsConnectResponse payload = new UserDtlsConnectResponse(response.correlationId());
        sendSignal(response.UserId(), payload);
    }

    public void sendError(String userId, ErrorResponse errorResponse) {
        sendSignal(userId, errorResponse);
    }

    private void sendSignal(String userId, SignalResponse payload) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/replies/" + payload.correlationId(), payload);
    }
}
