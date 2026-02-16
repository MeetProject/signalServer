package com.meetProject.signalserver.service;

import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.model.Room;
import com.meetProject.signalserver.model.dto.common.MediaOption;
import com.meetProject.signalserver.model.dto.common.Participant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class RoomsService {
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public void createRoom(Room room) {
        if(this.rooms.containsKey(room.getRoomId())) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_ALREADY_EXISTS);
        }
        rooms.put(room.getRoomId(), room);
    }

    public void addParticipant(String roomId, Participant participant) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_NOT_FOUND);
        }

        room.addParticipant(participant);
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

    public List<Participant> getParticipants(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_NOT_FOUND);
        }
        return room.getParticipants().values().stream().toList();
    }

    public boolean exists(String roomId) {
        return rooms.containsKey(roomId);
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public void handleHandUp(String roomId, String userId, boolean value) {
        Participant participant = getParticipant(roomId, userId);
        participant.setHandUp(value);
    }

    public void handleMediaOption(String roomId, String userId, MediaOption value) {
        Participant participant = getParticipant(roomId, userId);
        participant.setMediaOption(value);
    }

    public void addProducer(String roomId, String userId, String producerId) {
        Participant participant = getParticipant(roomId, userId);
        participant.addProducerId(producerId);
    }

    public void removeProducer(String roomId, String userId, String producerId) {
        Participant participant = getParticipant(roomId, userId);
        participant.removeProducerId(producerId);
    }

    private Participant getParticipant(String roomId, String userId) {
        Room room = rooms.get(roomId);

        if(room == null) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_NOT_FOUND);
        }

        Participant participant = room.getParticipants().get(userId);

        if(participant == null) {
            throw new IllegalArgumentException(ErrorMessage.USER_NOT_JOINED);
        }

        return participant;
    }
}
