package com.meetProject.signalserver.model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private final String roomID;
    private final List<User> participants;

    public Room(String roomID, User host) {
        this.roomID = roomID;
        this.participants = new ArrayList<>(List.of(host));
    }

    public String getRoomID() {
        return roomID;
    }

    public List<User> getParticipants() {
        return List.copyOf(participants);
    }

    public void addParticipant(User participant) {
        participants.add(participant);
    }

    public void removeParticipant(User participant) {
        participants.remove(participant);
    }
}
