package com.meetProject.signalserver.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.meetProject.signalserver.exception.InvalidInputException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class MediaOptionTest {
    @Test
    @DisplayName("오디오/비디오 상태를 담는다")
    void holdsAudioVideo() {
        MediaOption option = new MediaOption(true, false);
        assertThat(option.audio()).isTrue();
        assertThat(option.video()).isFalse();
    }

    @ParameterizedTest(name = "audio={0}, video={1}")
    @CsvSource(nullValues = "null", value = {
            "null, true",
            "true, null",
            "null, null"
    })
    @DisplayName("오디오/비디오가 널이면 예외")
    void rejectsNull(Boolean audio, Boolean video) {
        assertThatThrownBy(() -> new MediaOption(audio, video)).isInstanceOf(InvalidInputException.class);
    }
}
