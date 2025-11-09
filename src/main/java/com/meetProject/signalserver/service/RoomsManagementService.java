package com.meetProject.signalserver.service;

import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.model.Room;
import com.meetProject.signalserver.model.User;
import java.util.HashMap;
import java.util.List;

public class RoomsManagementService {
    private final HashMap<String, Room> rooms = new HashMap<>();

    public void createRoom(Room room) {
        rooms.put(room.getRoomID(), room);
    }

    public void addParticipant(String RoomId, User user) {
        Room room = rooms.get(RoomId);
        if (room == null) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_NOT_FOUND);
        }

        room.addParticipant(user);
    }

    public void removeParticipant(String RoomId, User user) {
        Room room = rooms.get(RoomId);

        if (room == null) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_NOT_FOUND);
        }

        room.removeParticipant(user);

        if(room.getParticipants().isEmpty()){
            rooms.remove(RoomId);
        }
    }

    public List<User> getParticipants(String RoomId) {
        Room room = rooms.get(RoomId);
        if (room == null) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_NOT_FOUND);
        }
        return room.getParticipants();
    }
}
