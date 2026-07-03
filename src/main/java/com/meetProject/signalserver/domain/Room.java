package com.meetProject.signalserver.domain;

import com.meetProject.signalserver.constant.RoomRule;
import com.meetProject.signalserver.util.RandomIdGenerator;
import java.time.LocalDateTime;

public class Room {
    private final String id;
    private final LocalDateTime createdAt;

    private Room(String id, LocalDateTime createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public static Room create() {
        String id = RandomIdGenerator.randomId(RoomRule.ROOM_ID_LENGTH);
        LocalDateTime now = LocalDateTime.now();

        return new Room(id, now);
    }

    public static Room restore(String id, LocalDateTime createdAt) {
        return new Room(id, createdAt);
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
