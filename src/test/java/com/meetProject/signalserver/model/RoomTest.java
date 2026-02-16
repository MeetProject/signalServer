package com.meetProject.signalserver.model;

import static org.assertj.core.api.Assertions.*;

import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.constant.RoomRule;
import com.meetProject.signalserver.model.dto.common.MediaOption;
import com.meetProject.signalserver.model.dto.common.Participant;
import com.meetProject.signalserver.model.dto.common.User;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RoomTest {

    private Room room;
    private final User user1 = new User("test1", "test1", "#ffffff", null);
    private final User user2 = new User("test2", "test2", "#ffffff", null);

    @BeforeEach
    void setUp() {
        room = new Room("room1");
        room.addParticipant(new Participant(user1, new MediaOption(true, true), List.of()));
        room.addParticipant(new Participant(user2, new MediaOption(true, true), List.of()));
    }

    @Test
    @DisplayName("Room 생성 및 초기 참가자 확인")
    void testRoomCreation() {
        assertThat(room.getRoomId()).isEqualTo("room1");
        List<String> id = room.getParticipants().values().stream().map(participant -> participant.getUser().userId()).toList();
        assertThat(id).containsExactlyInAnyOrder("test1", "test2");
    }

    @Test
    @DisplayName("중복 참가자 추가 시 예외")
    void testAddParticipantAlreadyJoined() {
        assertThatThrownBy(() -> room.addParticipant(new Participant(user1, new MediaOption(true, false), List.of())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.ROOM_ALREADY_JOINED);
    }

    @Test
    @DisplayName("최대 참가자 수 초과 시 예외")
    void testAddParticipantRoomFull() {
        Room maxRoom = new Room("roomMax");
        for (int i = 0; i < RoomRule.MAX_ROOM_PARTICIPANTS; i++) {
            User user = new User("test" + i, "test" + i,"#ffffff", null);
            Participant participant = new Participant(user, new MediaOption(true, true), List.of());
            maxRoom.addParticipant(participant);
        }

        User extraUser = new User("extraUser", "extraUser", "#ffffff", null);
        Participant extraParticipant = new Participant(extraUser, new MediaOption(true, true), List.of());

        assertThatThrownBy(() -> maxRoom.addParticipant(extraParticipant))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.ROOM_FULL);
    }

    @Test
    @DisplayName("참가자 제거")
    void testRemoveParticipant() {
        room.removeParticipant("user1");
        assertThat(room.getParticipants().values().stream().map(participant -> participant.getUser().userId())).doesNotContain("user1");
    }
}