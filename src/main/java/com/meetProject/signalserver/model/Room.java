package com.meetProject.signalserver.model;

import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.constant.RoomRule;
import java.util.ArrayList;
import java.util.List;

public class Room {
    private final String roomId;
    private final List<User> participants;

    public Room(String roomId,  List<User> participants) {
        this.roomId = roomId;
        this.participants = new ArrayList<>(List.copyOf(participants));
    }

    public String getRoomId() {
        return roomId;
    }

    public List<User> getParticipants() {
        return List.copyOf(participants);
    }

    public void addParticipant(User participant) {
        if(participants.size() >= RoomRule.MAX_ROOM_PARTICIPANTS) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_FULL);
        }
        participants.add(participant);
    }

    public void removeParticipant(User participant) {
        participants.remove(participant);
    }
}
