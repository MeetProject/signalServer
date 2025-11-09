package com.meetProject.signalserver.service;

import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.model.Room;
import com.meetProject.signalserver.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class RoomsManagementService {
    private final List<User> testUsers = new ArrayList<>(List.of(
            new User("test1", "#000000", "test1"),
            new User("test2", "#ffffff", "test2")
    ));
    private final Room testRoom = new Room("test", testUsers);
    private final Map<String, Room> rooms = new ConcurrentHashMap<>(Map.of(this.testRoom.getRoomId(), this.testRoom));

    public void createRoom(Room room) {
        rooms.put(room.getRoomId(), room);
    }

    public void addParticipant(String roomId, User user) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_NOT_FOUND);
        }

        room.addParticipant(user);
    }

    public void removeParticipant(String roomId, User user) {
        Room room = rooms.get(roomId);

        if (room == null) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_NOT_FOUND);
        }

        room.removeParticipant(user);

        if(room.getParticipants().isEmpty()){
            rooms.remove(roomId);
        }
    }

    public List<User> getParticipants(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_NOT_FOUND);
        }
        return room.getParticipants();
    }
}
