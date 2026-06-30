package com.meetProject.signalserver.service.message;

import com.meetProject.signalserver.domain.Participant;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.CapabilitiesResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ConsumerParamsResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.DtlsConnectResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.DtlsResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.RtlsResponse;
import com.meetProject.signalserver.domain.User;
import com.meetProject.signalserver.dto.socket.ErrorResponse;
import com.meetProject.signalserver.dto.common.SignalResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.JoinPayload;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.JoinResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserCapabilityResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserConsumerParamsResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserDtlsConnectResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserDtlsResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserRtlsResponse;
import java.util.List;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class SignalMessagingService {
    private final SimpMessagingTemplate messagingTemplate;

    public SignalMessagingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendJoin(User user, JoinPayload joinPayload, List<Participant> participants) {
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
        UserDtlsResponse payload = new UserDtlsResponse(response.correlationId(), response.options());
        sendSignal(response.userId(), payload);
    }

    public void sendDtlsConnect(DtlsConnectResponse response) {
        UserDtlsConnectResponse payload = new UserDtlsConnectResponse(response.correlationId());
        sendSignal(response.userId(), payload);
    }

    public void sendRtls(RtlsResponse response) {
        UserRtlsResponse payload = new UserRtlsResponse(response.correlationId(), response.producerId());
        sendSignal(response.userId(), payload);
    }

    public void sendConsumerParams(ConsumerParamsResponse response) {
        UserConsumerParamsResponse payload = new UserConsumerParamsResponse(response.correlationId(), response.consumerParams());
        sendSignal(response.userId(), payload);
    }

    public void sendError(String userId, ErrorResponse errorResponse) {
        sendSignal(userId, errorResponse);
    }

    public void sendSignal(String userId, SignalResponse payload) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/replies", payload);
    }
}
