package com.meetProject.signalserver.service;

import com.meetProject.signalserver.model.Room;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RoomServiceTest {
    private RoomsService roomService;

    @BeforeEach
    void setUp() {
        roomService = new RoomsService();
    }

    @Test
    @DisplayName("방 생성 및 방 정보 얻기 테스트")
    void createAndGetRoom() {
        Room room = new Room("room1", new ArrayList<>());
        roomService.createRoom(room);

        Room r = roomService.getRoom("room1");
        assertNotNull(r);
        assertEquals(room, r);
    }

    @Test
    @DisplayName("방 참가자 추가 테스트")
    void addParticipant() {
        Room room = new Room("room1", new ArrayList<>());
        roomService.createRoom(room);

        roomService.addParticipant("room1", "user1");
        Room r = roomService.getRoom("room1");

        assertNotNull(r);
        assertEquals(1, r.getParticipants().size());
        assertEquals(List.of("user1"), r.getParticipants());
    }

    @Test
    @DisplayName("방 참가자 삭제 테스트")
    void removeParticipant() {
        Room room = new Room("room1", new ArrayList<>(List.of("user1", "user2")));
        roomService.createRoom(room);

        roomService.removeParticipant("room1", "user1");
        Room r = roomService.getRoom("room1");

        assertNotNull(r);
        assertEquals(1, r.getParticipants().size());
        assertEquals(List.of("user2"), r.getParticipants());
    }

    @Test
    @DisplayName("방 존제 확인 테스트")
    void flagParticipant() {
        Room room = new Room("room1", new ArrayList<>(List.of("user1", "user2")));
        roomService.createRoom(room);

        assertTrue(roomService.exists("room1"));
        assertFalse(roomService.exists("room2"));
    }
}
