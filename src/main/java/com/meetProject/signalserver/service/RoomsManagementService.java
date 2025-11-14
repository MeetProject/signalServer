package com.meetProject.signalserver.service;

import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.model.Room;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class RoomsManagementService {
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public void createRoom(Room room) {
        if(this.rooms.containsKey(room.getRoomId())) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_ALREADY_EXISTS);
        }
        rooms.put(room.getRoomId(), room);
    }

    public void addParticipant(String roomId, String userId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_NOT_FOUND);
        }

        if(room.getParticipants().contains(userId)) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_ALREADY_JOINED);
        }

        room.addParticipant(userId);
    }

    public void removeParticipant(String roomId, String userId) {
        Room room = rooms.get(roomId);

        if (room == null) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_NOT_FOUND);
        }

        room.removeParticipant(userId);

        if(room.getParticipants().isEmpty()){
            rooms.remove(roomId);
        }
    }

    public List<String> getParticipants(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_NOT_FOUND);
        }
        return room.getParticipants();
    }

    public boolean isRoomNotExist(String roomId) {
        return !rooms.containsKey(roomId);
    }
}
