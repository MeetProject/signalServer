package com.meetProject.signalserver.model;

import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.constant.RoomRule;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Room {
    private final String roomId;
    private final Set<String> participants;

    public Room(String roomId,  List<String> participants) {
        this.roomId = roomId;
        this.participants = new HashSet<>(List.copyOf(participants));
    }

    public String getRoomId() {
        return roomId;
    }

    public List<String> getParticipants() {
        return List.copyOf(participants);
    }

    public void addParticipant(String participant) {
        if(participants.size() >= RoomRule.MAX_ROOM_PARTICIPANTS) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_FULL);
        }

        if(participants.contains(participant)) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_ALREADY_JOINED);
        }
        participants.add(participant);
    }

    public void removeParticipant(String participant) {
        participants.remove(participant);
    }
}
