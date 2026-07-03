package com.meetProject.signalserver.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.meetProject.signalserver.exception.InvalidInputException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

public class ProfileColorTest {
    @ParameterizedTest(name = "색상={0}")
    @ValueSource(strings = {"#fff", "#ffff", "#ffffff", "#ffffffff", "#AbC123"})
    @DisplayName("유효한 hex 색상을 허용한다")
    void allowsValidHex(String value) {
        assertThat(new ProfileColor(value).value()).isEqualTo(value);
    }

    @ParameterizedTest(name = "색상={0}")
    @NullSource
    @ValueSource(strings = {"fff", "#ff", "#fffff", "#gggggg", "ffffff", "#"})
    @DisplayName("형식에 맞지 않거나 널이면 예외")
    void rejectsInvalidColor(String value) {
        assertThatThrownBy(() -> new ProfileColor(value)).isInstanceOf(InvalidInputException.class);
    }
}
