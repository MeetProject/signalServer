package com.meetProject.signalserver.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.meetProject.signalserver.constant.RoomRule;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RoomTest {
    @Test
    @DisplayName("생성하면 규칙 길이의 id가 부여된다")
    void createsWithRuleLengthId() {
        Room room = Room.create();

        assertThat(room.getId()).hasSize(RoomRule.ROOM_ID_LENGTH);
        assertThat(room.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("생성할 때마다 id가 다르다")
    void createsUniqueId() {
        assertThat(Room.create().getId()).isNotEqualTo(Room.create().getId());
    }

    @Test
    @DisplayName("복원하면 주어진 값을 유지한다")
    void restoresGivenValues() {
        LocalDateTime now = LocalDateTime.now();
        Room room = Room.restore("ROOM123456", now);

        assertThat(room.getId()).isEqualTo("ROOM123456");
        assertThat(room.getCreatedAt()).isEqualTo(now);
    }
}
