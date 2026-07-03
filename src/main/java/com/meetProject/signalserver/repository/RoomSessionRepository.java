package com.meetProject.signalserver.repository;

import com.meetProject.signalserver.domain.Participant;
import com.meetProject.signalserver.domain.RoomSession;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Repository;

@Repository
public class RoomSessionRepository {
    private final Map<String, RoomSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> userRoom = new ConcurrentHashMap<>();

    public record LeaveResult(String roomId, boolean roomEmpty) {}

    public List<Participant> join(String roomId, Participant joiner) {
        AtomicReference<List<Participant>> others = new AtomicReference<>();
        sessions.compute(roomId, (id, session) -> {
            RoomSession target = session != null ? session : new RoomSession(id);
            others.set(target.join(joiner));
            return target;
        });
        userRoom.put(joiner.getUserId(), roomId);
        return others.get();
    }

    public Optional<LeaveResult> leave(String userId) {
        String roomId = userRoom.remove(userId);
        if (roomId == null) {
            return Optional.empty();
        }
        AtomicBoolean roomEmpty = new AtomicBoolean(true);
        sessions.computeIfPresent(roomId, (id, session) -> {
            session.leave(userId);
            roomEmpty.set(session.isEmpty());
            return session.isEmpty() ? null : session;
        });
        return Optional.of(new LeaveResult(roomId, roomEmpty.get()));
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
