package com.meetProject.signalserver.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.meetProject.signalserver.domain.Room;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

@JdbcTest
@Import(RoomRepository.class)
public class RoomRepositoryTest {
    @Autowired
    RoomRepository roomRepository;

    private final LocalDateTime createdAt = LocalDateTime.of(2026, 7, 3, 12, 0, 0);

    @Test
    @DisplayName("저장한 방을 id로 조회한다")
    void savesAndFinds() {
        roomRepository.save(Room.restore("ROOM123456", createdAt));

        assertThat(roomRepository.findById("ROOM123456")).hasValueSatisfying(room -> {
            assertThat(room.getId()).isEqualTo("ROOM123456");
            assertThat(room.getCreatedAt()).isEqualTo(createdAt);
        });
    }

    @Test
    @DisplayName("없는 방 조회는 빈 결과")
    void findMissingReturnsEmpty() {
        assertThat(roomRepository.findById("nope")).isEmpty();
    }

    @Test
    @DisplayName("존재 여부를 반환한다")
    void reportsExistence() {
        roomRepository.save(Room.restore("ROOM123456", createdAt));

        assertThat(roomRepository.existsById("ROOM123456")).isTrue();
        assertThat(roomRepository.existsById("nope")).isFalse();
    }

    @Test
    @DisplayName("방을 삭제한다")
    void deletesRoom() {
        roomRepository.save(Room.restore("ROOM123456", createdAt));

        roomRepository.deleteById("ROOM123456");

        assertThat(roomRepository.existsById("ROOM123456")).isFalse();
    }
}
