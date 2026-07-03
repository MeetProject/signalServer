package com.meetProject.signalserver.repository;

import com.meetProject.signalserver.domain.Participant;
import com.meetProject.signalserver.domain.RoomSession;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class RoomSessionRepository {
    private final Map<String, RoomSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> userRoom = new ConcurrentHashMap<>();

    public List<Participant> join(String roomId, Participant joiner) {
        RoomSession session = sessions.computeIfAbsent(roomId, RoomSession::new);
        List<Participant> others = session.join(joiner);
        userRoom.put(joiner.getUserId(), roomId);
        return others;
    }

    public Optional<String> leave(String userId) {
        Optional<String> roomId = Optional.ofNullable(userRoom.remove(userId));
        roomId.ifPresent(id -> sessions.computeIfPresent(id, (key, session) -> {
            session.leave(userId);
            return session.isEmpty() ? null : session;
        }));
        return roomId;
    }

    public Optional<RoomSession> find(String roomId) {
        return Optional.ofNullable(sessions.get(roomId));
    }

    public Optional<String> findRoomIdByUserId(String userId) {
        return Optional.ofNullable(userRoom.get(userId));
    }

    public Optional<RoomSession> findByUserId(String userId) {
        return findRoomIdByUserId(userId).flatMap(this::find);
    }
}
