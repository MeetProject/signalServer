package com.meetProject.signalserver.repository;

import com.meetProject.signalserver.domain.Participant;
import com.meetProject.signalserver.exception.ParticipantNotJoinedException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class ParticipantRepository {
    private final Map<String, Map<String, Participant>> rooms = new ConcurrentHashMap<>();
    private final Map<String, String> userRoom = new ConcurrentHashMap<>();

    public List<Participant> add(String roomId, Participant participant) {
        List<Participant> participants = rooms.get(roomId)
                .values()
                .stream()
                .toList();

        rooms.computeIfAbsent(roomId, key -> new ConcurrentHashMap<>())
                .put(participant.getUserId(), participant);
        userRoom.put(participant.getUserId(), roomId);

        return participants;
    }

    public void remove(String roomId, String userId) {
        rooms.computeIfPresent(roomId, (id, members) -> {
            members.remove(userId);
            if (members.isEmpty()) {
                return null;
            }
            return members;
        });
        userRoom.remove(userId);
    }

    public void addProducer(String roomId, String userId, String producerId) {
        Participant participant = getParticipant(roomId, userId);
        participant.addProducerId(producerId);
    }

    public void removeProducerId(String roomId, String userId, String producerId) {
        Participant participant = getParticipant(roomId, userId);
        participant.removeProducerId(producerId);
    }

    public boolean toggleHandsUp(String roomId, String userId) {
        Participant participant = getParticipant(roomId, userId);
        return participant.toggleHandsUp();
    }

    public void toggleAudio(String roomId, String userId) {
        Participant participant = getParticipant(roomId, userId);
        participant.toggleAudio();
    }

    public void toggleVideo(String roomId, String userId) {
        Participant participant = getParticipant(roomId, userId);
        participant.toggleVideo();
    }

    public Optional<List<Participant>> findByRoom(String roomId) {
        if(rooms.containsKey(roomId)) {
            List<Participant> participants = rooms.get(roomId).values()
                    .stream()
                    .toList();
            return Optional.of(participants);
        }

        return Optional.empty();
    }

    public long countByRoom(String roomId) {
        if(!rooms.containsKey(roomId)) {
            return 0;
        }

        return rooms.get(roomId).size();
    }

    public Optional<String> findRoomIdByUserId(String userId) {
        if(userRoom.containsKey(userId)) {
            return Optional.of(userRoom.get(userId));
        }
        return Optional.empty();
    }

    private Participant getParticipant(String roomId, String userId) {
        if(!rooms.containsKey(roomId)) {
            throw new ParticipantNotJoinedException();
        }

        if(!rooms.get(roomId).containsKey(userId)) {
            throw new ParticipantNotJoinedException();
        }

        return rooms.get(roomId).get(userId);
    }
}
