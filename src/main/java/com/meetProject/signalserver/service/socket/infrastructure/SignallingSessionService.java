package com.meetProject.signalserver.service.socket.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.BinaryMessage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.web.socket.WebSocketSession;

@Service
public class SignallingSessionService {
    private final ConcurrentMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Object sendLock = new Object();

    public void addSession(String userId, WebSocketSession session) {
        sessions.put(userId, session);
    }

    public void removeSession(String userId) {
        sessions.remove(userId);
    }

    public WebSocketSession getSession(String userId) {
        return sessions.get(userId);
    }

    private void send(WebSocketSession session, Object payload) throws IOException {
        synchronized (sendLock) {
            if(session == null || !session.isOpen()) {
                return;
            }

            try {
                byte[] bytes = objectMapper.writeValueAsBytes(payload);
                session.sendMessage(new BinaryMessage(bytes, true));
            } catch (IOException e) {
                System.err.println("Failed to send message to session " + session.getId() + ": " + e.getMessage());
            }
        }
    }

    public void sendToUser(String userId, Object payload) throws IOException {
        WebSocketSession session = getSession(userId);
        send(session, payload);
    }

    public void sendToRoom(String userId, String roomId, Object payload) throws IOException {
        for(WebSocketSession session : sessions.values()) {
            Object attr = session.getAttributes().get("userId");
            if (!(attr instanceof String participantId)) {
                continue;
            }
            if(session.isOpen() && !participantId.equals(userId)) {
                send(session, payload);
            }
        }
    }
}
