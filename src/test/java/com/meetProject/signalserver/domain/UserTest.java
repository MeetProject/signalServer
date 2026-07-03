package com.meetProject.signalserver.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.meetProject.signalserver.exception.InvalidInputException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserTest {
    @Test
    @DisplayName("생성하면 uuid id와 값객체를 가진다")
    void createsWithUuidAndValueObjects() {
        User user = User.create("홍길동", "#ffffff");

        assertThat(user.getId()).isNotBlank().contains("-");
        assertThat(user.getName()).isEqualTo("홍길동");
        assertThat(user.getProfileColor()).isEqualTo("#ffffff");
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("이름이 유효하지 않으면 생성 실패")
    void failsOnInvalidName() {
        assertThatThrownBy(() -> User.create("", "#ffffff")).isInstanceOf(InvalidInputException.class);
    }

    @Test
    @DisplayName("색상이 유효하지 않으면 생성 실패")
    void failsOnInvalidColor() {
        assertThatThrownBy(() -> User.create("홍길동", "red")).isInstanceOf(InvalidInputException.class);
    }

    @Test
    @DisplayName("복원하면 주어진 값을 유지한다")
    void restoresGivenValues() {
        LocalDateTime now = LocalDateTime.now();
        User user = User.restore("uid-1", "김철수", "#000000", now);

        assertThat(user.getId()).isEqualTo("uid-1");
        assertThat(user.getName()).isEqualTo("김철수");
        assertThat(user.getProfileColor()).isEqualTo("#000000");
        assertThat(user.getCreatedAt()).isEqualTo(now);
    }
}
