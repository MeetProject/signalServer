package com.meetProject.signalserver.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.meetProject.signalserver.exception.InvalidInputException;
import com.meetProject.signalserver.fake.FakeUserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class UserServiceTest {
    private final FakeUserRepository userRepository = new FakeUserRepository();
    private final UserService userService = new UserService(userRepository);

    @Test
    @DisplayName("사용자를 생성하고 저장한다")
    void createsAndSavesUser() {
        String id = userService.create("홍길동", "#ffffff");

        assertThat(id).isNotBlank();
        assertThat(userRepository.findById(id)).hasValueSatisfying(user -> {
            assertThat(user.getName()).isEqualTo("홍길동");
            assertThat(user.getProfileColor()).isEqualTo("#ffffff");
        });
    }

    @Test
    @DisplayName("생성할 때마다 다른 id가 부여된다")
    void assignsUniqueIds() {
        String first = userService.create("홍길동", "#ffffff");
        String second = userService.create("홍길동", "#ffffff");

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    @DisplayName("사용자를 삭제한다")
    void deletesUser() {
        String id = userService.create("홍길동", "#ffffff");

        userService.delete(id);

        assertThat(userRepository.existsById(id)).isFalse();
    }

    @Test
    @DisplayName("없는 사용자 삭제는 무시한다")
    void ignoresDeletingUnknownUser() {
        userService.delete("nope");

        assertThat(userRepository.existsById("nope")).isFalse();
    }

    @ParameterizedTest(name = "이름={0}, 색상={1}")
    @CsvSource(nullValues = "null", value = {
            "'', #ffffff",
            "aaaaaaaaaaaaaaaaaaaaa, #ffffff",
            "홍길동, red",
            "홍길동, null"
    })
    @DisplayName("유효하지 않은 입력이면 예외")
    void rejectsInvalidInput(String name, String color) {
        assertThatThrownBy(() -> userService.create(name, color))
                .isInstanceOf(InvalidInputException.class);
    }
}
