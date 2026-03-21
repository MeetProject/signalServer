package com.meetProject.signalserver.service;

import com.meetProject.signalserver.model.Room;
import com.meetProject.signalserver.model.dto.common.MediaOption;
import com.meetProject.signalserver.model.dto.common.Participant;
import com.meetProject.signalserver.model.dto.common.User;
import java.util.ArrayList;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RoomServiceTest {
    private RoomsService roomService;

    @BeforeEach
    void setUp() {
        roomService = new RoomsService();
        Room room = new Room("room1");
        roomService.createRoom(room);
    }

    @Test
    @DisplayName("방 생성 및 방 정보 얻기 테스트")
    void createAndGetRoom() {
        Room r = roomService.getRoom("room1");
        assertThat(r).isNotNull();
        assertThat(r.getRoomId()).isEqualTo("room1");
    }

    @Test
    @DisplayName("방 참가자 추가 테스트")
    void addParticipant() {
        User user = new User("user1", "user1", "#ffffff", null);
        Participant participant = new Participant(user, new MediaOption(true, true), List.of());
        roomService.addParticipant("room1", participant);
        Room r = roomService.getRoom("room1");

        assertThat(r).isNotNull();
        assertThat(r.getParticipants().size()).isEqualTo(1);
        assertThat(r.getParticipants().values().stream().map(p -> p.getUser().userId())).isEqualTo(List.of("user1"));
    }

    @Test
    @DisplayName("방 참가자 삭제 테스트")
    void removeParticipant() {
        User user1 = new User("user1", "user1", "#ffffff", null);
        Participant participant1 = new Participant(user1, new MediaOption(true, true), List.of());

        User user2 = new User("user2", "user2", "#ffffff", null);
        Participant participant2 = new Participant(user2, new MediaOption(true, true), List.of());

        roomService.addParticipant("room1", participant1);
        roomService.addParticipant("room1", participant2);

        roomService.removeParticipant("room1", "user1");
        Room r = roomService.getRoom("room1");

        assertThat(r).isNotNull();
        assertThat(r.getParticipants().size()).isEqualTo(1);
        assertThat(r.getParticipants().values().stream().map(participant -> participant.getUser().userId())).isEqualTo(List.of("user2"));
    }

    @Test
    @DisplayName("방 존재 확인 테스트")
    void flagParticipant() {
        assertThat(roomService.exists("room1")).isTrue();
        assertThat(roomService.exists("room2")).isFalse();
    }
}
