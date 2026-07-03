package com.meetProject.signalserver.domain;

import com.meetProject.signalserver.constant.RoomRule;
import com.meetProject.signalserver.exception.ParticipantNotJoinedException;
import com.meetProject.signalserver.exception.RoomFullException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RoomSession {
    private final String roomId;
    private final Map<String, Participant> participants = new ConcurrentHashMap<>();

    public RoomSession(String roomId) {
        this.roomId = roomId;
    }

    public synchronized List<Participant> join(Participant joiner) {
        if (participants.size() >= RoomRule.MAX_ROOM_PARTICIPANTS) {
            throw new RoomFullException();
        }
        List<Participant> others = List.copyOf(participants.values());
        participants.put(joiner.getUserId(), joiner);
        return others;
    }

    public boolean leave(String userId) {
        participants.remove(userId);
        return participants.isEmpty();
    }

    public boolean isEmpty() {
        return participants.isEmpty();
    }

    public boolean isFull() {
        return participants.size() >= RoomRule.MAX_ROOM_PARTICIPANTS;
    }

    public List<Participant> participants() {
        return List.copyOf(participants.values());
    }

    public boolean toggleHandsUp(String userId) {
        return participant(userId).toggleHandsUp();
    }

    public void updateMediaOption(String userId, MediaOption mediaOption) {
        participant(userId).updateMediaOption(mediaOption);
    }

    public void addProducer(String userId, String producerId) {
        participant(userId).addProducerId(producerId);
    }

    public void removeProducer(String userId, String producerId) {
        participant(userId).removeProducerId(producerId);
    }

    public String getRoomId() {
        return roomId;
    }

    private Participant participant(String userId) {
        Participant participant = participants.get(userId);
        if (participant == null) {
            throw new ParticipantNotJoinedException();
        }
        return participant;
    }
}
