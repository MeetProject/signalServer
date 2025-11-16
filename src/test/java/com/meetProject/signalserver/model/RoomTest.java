package com.meetProject.signalserver.model;

import static org.assertj.core.api.Assertions.*;

import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.constant.RoomRule;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RoomTest {

    private Room room;

    @BeforeEach
    void setUp() {
        room = new Room("room1", List.of("user1", "user2"));
    }

    @Test
    @DisplayName("Room 생성 및 초기 참가자 확인")
    void testRoomCreation() {
        assertThat(room.getRoomId()).isEqualTo("room1");
        assertThat(room.getParticipants()).containsExactlyInAnyOrder("user1", "user2");
    }

    @Test
    @DisplayName("참가자 추가 정상")
    void testAddParticipant() {
        room.addParticipant("user3");
        assertThat(room.getParticipants()).contains("user3");
    }

    @Test
    @DisplayName("중복 참가자 추가 시 예외")
    void testAddParticipantAlreadyJoined() {
        assertThatThrownBy(() -> room.addParticipant("user1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.ROOM_ALREADY_JOINED);
    }

    @Test
    @DisplayName("최대 참가자 수 초과 시 예외")
    void testAddParticipantRoomFull() {
        Room maxRoom = new Room("roomMax", List.of());
        for (int i = 0; i < RoomRule.MAX_ROOM_PARTICIPANTS; i++) {
            maxRoom.addParticipant("user" + i);
        }

        assertThatThrownBy(() -> maxRoom.addParticipant("extraUser"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.ROOM_FULL);
    }

    @Test
    @DisplayName("참가자 제거")
    void testRemoveParticipant() {
        room.removeParticipant("user1");
        assertThat(room.getParticipants()).doesNotContain("user1");
    }
}