package com.meetProject.signalserver.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.meetProject.signalserver.domain.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

@JdbcTest
@Import(UserRepository.class)
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    private final LocalDateTime createdAt = LocalDateTime.of(2026, 7, 3, 12, 0, 0);

    @Test
    @DisplayName("저장한 사용자를 id로 조회한다")
    void savesAndFinds() {
        userRepository.save(User.restore("uid-1", "홍길동", "#ffffff", createdAt));

        assertThat(userRepository.findById("uid-1")).hasValueSatisfying(user -> {
            assertThat(user.getId()).isEqualTo("uid-1");
            assertThat(user.getName()).isEqualTo("홍길동");
            assertThat(user.getProfileColor()).isEqualTo("#ffffff");
            assertThat(user.getCreatedAt()).isEqualTo(createdAt);
        });
    }

    @Test
    @DisplayName("없는 사용자 조회는 빈 결과")
    void findMissingReturnsEmpty() {
        assertThat(userRepository.findById("nope")).isEmpty();
    }

    @Test
    @DisplayName("존재 여부를 반환한다")
    void reportsExistence() {
        userRepository.save(User.restore("uid-1", "홍길동", "#ffffff", createdAt));

        assertThat(userRepository.existsById("uid-1")).isTrue();
        assertThat(userRepository.existsById("nope")).isFalse();
    }

    @Test
    @DisplayName("사용자를 삭제한다")
    void deletesUser() {
        userRepository.save(User.restore("uid-1", "홍길동", "#ffffff", createdAt));

        userRepository.deleteById("uid-1");

        assertThat(userRepository.existsById("uid-1")).isFalse();
    }
}
