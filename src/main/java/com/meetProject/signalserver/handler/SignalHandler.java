package com.meetProject.signalserver.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetProject.signalserver.model.dto.AnswerPayload;
import com.meetProject.signalserver.model.dto.IcePayload;
import com.meetProject.signalserver.model.dto.JoinPayload;
import com.meetProject.signalserver.model.dto.OfferPayload;
import com.meetProject.signalserver.model.dto.TrackPayload;
import com.meetProject.signalserver.orchestration.JoinOrchestrationService;
import com.meetProject.signalserver.orchestration.LeaveOrchestrationService;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.service.UserService;
import org.springframework.stereotype.Component;


@Component
public class SignalHandler {
    private final SignalMessagingService signalMessagingService;
    private final JoinOrchestrationService joinOrchestrationService;
    private final LeaveOrchestrationService leaveOrchestrationService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public SignalHandler(SignalMessagingService signalMessagingService, JoinOrchestrationService joinService, LeaveOrchestrationService leaveService, UserService userService,  ObjectMapper objectMapper) {
        this.signalMessagingService = signalMessagingService;
        this.joinOrchestrationService = joinService;
        this.leaveOrchestrationService = leaveService;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    public void handleMessage(String userId, String path, Object payload) {
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
            case "TRACK" -> {
                track(userId, payload);
            }
        }

    }

    private void join(String userId, Object payload) {
        JoinPayload joinPayload = objectMapper.convertValue(payload, JoinPayload.class);
        joinOrchestrationService.joinRoom(userId, joinPayload.roomId(), joinPayload.mediaOption());
    }

    private void offer(String userId, Object payload) {
        OfferPayload offerPayload = objectMapper.convertValue(payload, OfferPayload.class);
        if(userId.equals("mediaServer")) {
            String roomId = userService.getRoomId(offerPayload.userId());
            signalMessagingService.sendOffer(userId, offerPayload.userId(), roomId, offerPayload.sdp());
        }
        String roomId = userService.getRoomId(userId);
        signalMessagingService.sendOffer(userId, offerPayload.userId(), roomId, offerPayload.sdp());
    }

    private void answer(String userId, Object payload) {
        AnswerPayload answerPayload = objectMapper.convertValue(payload, AnswerPayload.class);
        if(userId.equals("mediaServer")) {
            String roomId = userService.getRoomId(answerPayload.userId());
            signalMessagingService.sendAnswer(userId, answerPayload.userId(), answerPayload.sdp(), roomId);
            return;
        }
        String roomId = userService.getRoomId(userId);
        signalMessagingService.sendAnswer(userId, answerPayload.userId(), answerPayload.sdp(), roomId);
    }

    private void ice(String userId, Object payload) {
        IcePayload icePayload = objectMapper.convertValue(payload, IcePayload.class);
        signalMessagingService.sendICE(userId, icePayload.userId(), icePayload.ice());
    }

    private void leave(String userId) {
        leaveOrchestrationService.leaveUser(userId);
    }

    private void track(String userId, Object payload) {
        TrackPayload trackPayload = objectMapper.convertValue(payload, TrackPayload.class);
        String roomId = userService.getRoomId(userId);
        signalMessagingService.sendTrack(userId, trackPayload.userId(), roomId, trackPayload.transceiver());
    }

}
