package com.meetProject.signalserver.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetProject.signalserver.model.socket.signal.AnswerPayload;
import com.meetProject.signalserver.model.socket.signal.IcePayload;
import com.meetProject.signalserver.model.socket.signal.JoinPayload;
import com.meetProject.signalserver.model.socket.signal.OfferPayload;
import com.meetProject.signalserver.orchestration.JoinOrchestrationService;
import com.meetProject.signalserver.orchestration.LeaveOrchestrationService;
import com.meetProject.signalserver.service.SignalMessagingService;
import java.io.IOException;
import org.springframework.stereotype.Component;


@Component
public class SignalHandler {
    private final SignalMessagingService signalMessagingService;
    private final JoinOrchestrationService joinOrchestrationService;
    private final LeaveOrchestrationService leaveOrchestrationService;
    private final ObjectMapper objectMapper;

    public SignalHandler(SignalMessagingService signalMessagingService, JoinOrchestrationService joinService, LeaveOrchestrationService leaveService, ObjectMapper objectMapper) {
        this.signalMessagingService = signalMessagingService;
        this.joinOrchestrationService = joinService;
        this.leaveOrchestrationService = leaveService;
        this.objectMapper = objectMapper;
    }

    public void handleMessage(String userId, String path, Object payload) throws IOException {
        switch (path) {
            case "JOIN" -> {
                join(userId, payload);
            }
            case "OFFER" -> {
                offer(userId, payload);
            }
            case "ANSWER" -> {
                answer(userId, payload);
            }
            case "ICE" -> {
                ice(userId, payload);
            }
            case "LEAVE" -> {
                leave(userId);
            }
            case "NEGOTIATION" -> {
                negotiation(userId);
            }
        }

    }

    private void join(String userId, Object payload) throws IOException {
        JoinPayload joinPayload = objectMapper.convertValue(payload, JoinPayload.class);
        joinOrchestrationService.joinRoom(userId, joinPayload.roomId(), joinPayload.mediaOption());
    }

    private void offer(String userId, Object payload) throws IOException {
        OfferPayload offerPayload = objectMapper.convertValue(payload, OfferPayload.class);
        signalMessagingService.sendOffer(offerPayload.userId(), offerPayload.sdp(), offerPayload.trackInfo());
    }

    private void answer(String userId, Object payload) throws IOException {
        AnswerPayload answerPayload = objectMapper.convertValue(payload, AnswerPayload.class);
        signalMessagingService.sendAnswer(userId, answerPayload.sdp(), answerPayload.trackInfo());
    }

    private void ice(String userId, Object payload) throws IOException {
        IcePayload icePayload = objectMapper.convertValue(payload, IcePayload.class);
        signalMessagingService.sendICE(userId, icePayload.userId(), icePayload.ice());
    }

    private void leave(String userId) throws IOException {
        leaveOrchestrationService.leaveUser(userId);
    }

    private void negotiation(String userId) throws IOException {
        signalMessagingService.sendNegotiation(userId);
    }

}
