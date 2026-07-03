package com.meetProject.signalserver.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.meetProject.signalserver.exception.InvalidInputException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

public class UserNameTest {
    @Test
    @DisplayName("유효한 이름을 담는다")
    void holdsValidName() {
        assertThat(new UserName("홍길동").name()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("경계값 20자는 허용한다")
    void allowsBoundaryLength() {
        String twenty = "a".repeat(20);
        assertThat(new UserName(twenty).name()).isEqualTo(twenty);
    }

    @ParameterizedTest(name = "이름={0}")
    @NullSource
    @ValueSource(strings = {"", "   ", "aaaaaaaaaaaaaaaaaaaaa"})
    @DisplayName("널·공백·길이 초과 이름은 예외")
    void rejectsInvalidName(String name) {
        assertThatThrownBy(() -> new UserName(name)).isInstanceOf(InvalidInputException.class);
    }
}
