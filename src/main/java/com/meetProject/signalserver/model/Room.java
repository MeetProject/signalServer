package com.meetProject.signalserver.model;

import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.constant.RoomRule;
import com.meetProject.signalserver.model.dto.common.Participant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

public class Room {
    @Getter
    private final String roomId;
    private final Map<String, Participant> participants = new ConcurrentHashMap<>();

    public Room(String roomId) {
        this.roomId = roomId;
    }

    public HashMap<String, Participant> getParticipants() {
        return new HashMap<>(participants);
    }

    public void addParticipant(Participant participant) {
        if(participants.size() >= RoomRule.MAX_ROOM_PARTICIPANTS) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_FULL);
        }

        if(participants.get(participant.getUser().userId()) != null) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_ALREADY_JOINED);
        }
        participants.put(participant.getUser().userId(), participant);
    }

    public void removeParticipant(String participant) {
        participants.remove(participant);
    }
}
