package com.meetProject.signalserver.service.socket.infrastructure;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
public class WebSocketUserService {

    // userId -> WebSocketSession
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public void registerUser(String userId, WebSocketSession session) {
        sessions.put(userId, session);
    }

    public void unregisterUser(String userId) {
        sessions.remove(userId);
    }

    public boolean isUserConnected(String userId) {
        return sessions.containsKey(userId);
    }

    public WebSocketSession getSession(String userId) {
        return sessions.get(userId);
    }
}