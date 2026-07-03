package com.meetProject.signalserver.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.meetProject.signalserver.domain.MediaOption;
import com.meetProject.signalserver.domain.Participant;
import com.meetProject.signalserver.domain.User;
import com.meetProject.signalserver.dto.application.DeviceResult;
import com.meetProject.signalserver.dto.application.HandsUpResult;
import com.meetProject.signalserver.dto.application.ProducerResult;
import com.meetProject.signalserver.exception.ParticipantNotJoinedException;
import com.meetProject.signalserver.repository.RoomSessionRepository;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class ParticipantServiceTest {
    private final RoomSessionRepository roomSessionRepository = new RoomSessionRepository();
    private final ParticipantService participantService = new ParticipantService(roomSessionRepository);
    private final MediaOption mediaOption = new MediaOption(true, true);

    private void join(String userId) {
        roomSessionRepository.join("roomA", Participant.join(
                User.restore(userId, "name", "#ffffff", LocalDateTime.now()), mediaOption));
    }

    private Participant participant(String userId) {
        return roomSessionRepository.find("roomA").orElseThrow().participants().stream()
                .filter(p -> p.getUserId().equals(userId))
                .findFirst()
                .orElseThrow();
    }

    @Test
    @DisplayName("프로듀서를 등록한다")
    void registersProducer() {
        join("u1");

        ProducerResult result = participantService.registerProducer("u1", "p1");

        assertThat(result).isEqualTo(new ProducerResult("roomA", "u1", "p1"));
        assertThat(participant("u1").getProducerIds()).containsExactly("p1");
    }

    @Test
    @DisplayName("프로듀서는 대상 참여자에게만 누적된다")
    void accumulatesProducersPerParticipant() {
        join("u1");
        join("u2");

        participantService.registerProducer("u1", "p1");
        participantService.registerProducer("u1", "p2");

        assertThat(participant("u1").getProducerIds()).containsExactlyInAnyOrder("p1", "p2");
        assertThat(participant("u2").getProducerIds()).isEmpty();
    }

    @Test
    @DisplayName("프로듀서를 제거한다")
    void removesProducer() {
        join("u1");
        participantService.registerProducer("u1", "p1");

        ProducerResult result = participantService.removeProducer("u1", "p1");

        assertThat(result).isEqualTo(new ProducerResult("roomA", "u1", "p1"));
        assertThat(participant("u1").getProducerIds()).isEmpty();
    }

    @Test
    @DisplayName("등록되지 않은 프로듀서 제거는 무시한다")
    void ignoresRemovingUnknownProducer() {
        join("u1");
        participantService.registerProducer("u1", "p1");

        ProducerResult result = participantService.removeProducer("u1", "nope");

        assertThat(result).isEqualTo(new ProducerResult("roomA", "u1", "nope"));
        assertThat(participant("u1").getProducerIds()).containsExactly("p1");
    }

    @Test
    @DisplayName("손들기를 토글한다")
    void togglesHandsUp() {
        join("u1");

        assertThat(participantService.toggleHandsUp("u1")).isEqualTo(new HandsUpResult("roomA", "u1", true));
        assertThat(participantService.toggleHandsUp("u1")).isEqualTo(new HandsUpResult("roomA", "u1", false));
    }

    @Test
    @DisplayName("디바이스 옵션을 갱신하면 참여자 상태에 반영된다")
    void updatesDevice() {
        join("u1");
        MediaOption updated = new MediaOption(false, false);

        DeviceResult result = participantService.updateDevice("u1", updated);

        assertThat(result).isEqualTo(new DeviceResult("roomA", "u1", updated));
        assertThat(participant("u1").getMediaOption()).isEqualTo(updated);
    }

    @Test
    @DisplayName("참여 중인 방 id를 반환한다")
    void returnsJoinedRoomId() {
        join("u1");

        assertThat(participantService.getJoinedRoomId("u1")).isEqualTo("roomA");
    }

    @ParameterizedTest
    @MethodSource("notJoinedActions")
    @DisplayName("참여하지 않은 사용자는 예외")
    void rejectsNotJoined(Consumer<ParticipantService> action) {
        assertThatThrownBy(() -> action.accept(participantService))
                .isInstanceOf(ParticipantNotJoinedException.class);
    }

    static Stream<Named<Consumer<ParticipantService>>> notJoinedActions() {
        return Stream.of(
                Named.of("registerProducer", service -> service.registerProducer("ghost", "p1")),
                Named.of("removeProducer", service -> service.removeProducer("ghost", "p1")),
                Named.of("toggleHandsUp", service -> service.toggleHandsUp("ghost")),
                Named.of("updateDevice", service -> service.updateDevice("ghost", new MediaOption(true, true))),
                Named.of("getJoinedRoomId", service -> service.getJoinedRoomId("ghost"))
        );
    }
}
