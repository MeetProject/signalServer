package com.meetProject.signalserver.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ParticipantTest {
    private final User user = User.restore("uid-1", "홍길동", "#ffffff", LocalDateTime.now());
    private final MediaOption mediaOption = new MediaOption(true, true);

    @Test
    @DisplayName("참여 시 기본값은 손내림/빈 프로듀서")
    void joinsWithDefaults() {
        Participant participant = Participant.join(user, mediaOption);

        assertThat(participant.isHandsUp()).isFalse();
        assertThat(participant.getMediaOption()).isEqualTo(mediaOption);
        assertThat(participant.getProducerIds()).isEmpty();
        assertThat(participant.getUserId()).isEqualTo("uid-1");
        assertThat(participant.getUserName()).isEqualTo("홍길동");
    }

    @Test
    @DisplayName("손들기 토글은 상태를 반전한다")
    void togglesHandsUp() {
        Participant participant = Participant.join(user, mediaOption);

        assertThat(participant.toggleHandsUp()).isTrue();
        assertThat(participant.isHandsUp()).isTrue();
        assertThat(participant.toggleHandsUp()).isFalse();
        assertThat(participant.isHandsUp()).isFalse();
    }

    @Test
    @DisplayName("프로듀서를 추가하고 제거한다")
    void addsAndRemovesProducer() {
        Participant participant = Participant.join(user, mediaOption);

        participant.addProducerId("p1");
        participant.addProducerId("p2");
        assertThat(participant.getProducerIds()).containsExactlyInAnyOrder("p1", "p2");

        participant.removeProducerId("p1");
        assertThat(participant.getProducerIds()).containsExactly("p2");
    }

    @Test
    @DisplayName("같은 프로듀서는 중복 저장되지 않는다")
    void ignoresDuplicateProducer() {
        Participant participant = Participant.join(user, mediaOption);

        participant.addProducerId("p1");
        participant.addProducerId("p1");

        assertThat(participant.getProducerIds()).containsExactly("p1");
    }

    @Test
    @DisplayName("없는 프로듀서 제거는 무시한다")
    void ignoresRemovingUnknownProducer() {
        Participant participant = Participant.join(user, mediaOption);
        participant.addProducerId("p1");

        participant.removeProducerId("nope");

        assertThat(participant.getProducerIds()).containsExactly("p1");
    }

    @Test
    @DisplayName("미디어옵션을 갱신한다")
    void updatesMediaOption() {
        Participant participant = Participant.join(user, mediaOption);
        MediaOption updated = new MediaOption(false, false);

        participant.updateMediaOption(updated);

        assertThat(participant.getMediaOption()).isEqualTo(updated);
    }
}
