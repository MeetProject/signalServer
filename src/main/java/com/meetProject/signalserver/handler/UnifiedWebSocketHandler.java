package com.meetProject.signalserver.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetProject.signalserver.service.socket.infrastructure.SignallingSessionService;
import com.meetProject.signalserver.service.socket.infrastructure.WebSocketUserService;
import java.io.IOException;
import java.util.Map;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class UnifiedWebSocketHandler implements WebSocketHandler {
    private final ObjectMapper objectMapper;
    private final SignallingSessionService sessionService;
    private final SignalHandler signalHandler;
    private final TopicHandler topicHandler;
    private final WebSocketUserService webSocketUserService;

    public UnifiedWebSocketHandler(ObjectMapper objectMapper, SignallingSessionService sessionService, SignalHandler signalHandler, TopicHandler topicHandler, WebSocketUserService webSocketUserService) {
        this.objectMapper = objectMapper;
        this.sessionService = sessionService;
        this.signalHandler = signalHandler;
        this.topicHandler = topicHandler;
        this.webSocketUserService = webSocketUserService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");
        System.out.println("connected: " + userId);
        if (userId == null || userId.isEmpty()) {
            return;
        }
        sessionService.addSession(userId, session);
        webSocketUserService.registerUser(userId, session);
    }

    @Override
    public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) throws Exception {
        String userId = (String) session.getAttributes().get("userId");
        if (userId == null || userId.isBlank()) {
            return;
        }

        byte[] payloadBytes;

        if (message instanceof TextMessage text) {
            payloadBytes = text.getPayload().getBytes();
        } else if (message instanceof org.springframework.web.socket.BinaryMessage binary) {
            payloadBytes = binary.getPayload().array();
        } else {
            return;
        }

        Map<String, Object> data =
                objectMapper.readValue(payloadBytes, new TypeReference<>() {});

        String type = (String) data.get("type");
        String path = (String) data.get("path");
        Map<String, Object> payload =
                objectMapper.convertValue(data.get("payload"), new TypeReference<>() {});

        switch (type) {
            case "signal" -> signalHandler.handleMessage(userId, path, payload);
            case "topic" -> topicHandler.handleMessage(userId, path, payload);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, @NonNull CloseStatus status) throws IOException {
        String userId = (String) session.getAttributes().get("userId");
        if (userId == null || userId.isBlank()) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }
        sessionService.removeSession(userId);
        webSocketUserService.unregisterUser(userId);
    }

    @Override
    public void handleTransportError(@NonNull WebSocketSession session, @NonNull Throwable exception) {}

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}