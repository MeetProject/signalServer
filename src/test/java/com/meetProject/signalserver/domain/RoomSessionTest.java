package com.meetProject.signalserver.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.meetProject.signalserver.constant.RoomRule;
import com.meetProject.signalserver.exception.ParticipantNotJoinedException;
import com.meetProject.signalserver.exception.RoomFullException;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class RoomSessionTest {
    private final MediaOption mediaOption = new MediaOption(true, true);

    private Participant participant(String userId) {
        return Participant.join(User.restore(userId, "name", "#ffffff", LocalDateTime.now()), mediaOption);
    }

    @Test
    @DisplayName("첫 참여자는 빈 기존목록을 받고 추가된다")
    void firstJoinerGetsEmptyOthers() {
        RoomSession session = new RoomSession("roomA");

        var others = session.join(participant("u1"));

        assertThat(others).isEmpty();
        assertThat(session.participants()).hasSize(1);
    }

    @Test
    @DisplayName("다음 참여자는 기존 참여자 목록을 받는다")
    void nextJoinerGetsExistingOthers() {
        RoomSession session = new RoomSession("roomA");
        Participant first = participant("u1");
        session.join(first);

        var others = session.join(participant("u2"));

        assertThat(others).containsExactly(first);
        assertThat(session.participants()).hasSize(2);
    }

    @Test
    @DisplayName("정원 경계에서 수용여부가 바뀐다")
    void capacityBoundaryTogglesAcceptance() {
        RoomSession session = new RoomSession("roomA");
        for (int i = 0; i < RoomRule.MAX_ROOM_PARTICIPANTS - 1; i++) {
            session.join(participant("u" + i));
        }
        assertThat(session.canAccept()).isTrue();

        session.join(participant("last"));

        assertThat(session.canAccept()).isFalse();
    }

    @Test
    @DisplayName("정원 초과 참여는 예외")
    void rejectsJoinWhenFull() {
        RoomSession session = new RoomSession("roomA");
        for (int i = 0; i < RoomRule.MAX_ROOM_PARTICIPANTS; i++) {
            session.join(participant("u" + i));
        }

        assertThatThrownBy(() -> session.join(participant("overflow")))
                .isInstanceOf(RoomFullException.class);
    }

    @Test
    @DisplayName("마지막 참여자 퇴장 시 빈 방으로 보고된다")
    void reportsEmptyOnLastLeave() {
        RoomSession session = new RoomSession("roomA");
        session.join(participant("u1"));

        boolean empty = session.leave("u1");

        assertThat(empty).isTrue();
        assertThat(session.isEmpty()).isTrue();
    }

    @Test
    @DisplayName("일부 퇴장은 빈 방이 아니다")
    void notEmptyWhenOthersRemain() {
        RoomSession session = new RoomSession("roomA");
        session.join(participant("u1"));
        session.join(participant("u2"));

        boolean empty = session.leave("u1");

        assertThat(empty).isFalse();
        assertThat(session.participants()).hasSize(1);
    }

    @ParameterizedTest
    @MethodSource("unknownParticipantActions")
    @DisplayName("참여하지 않은 사용자 조작은 예외")
    void rejectsUnknownParticipant(Consumer<RoomSession> action) {
        RoomSession session = new RoomSession("roomA");

        assertThatThrownBy(() -> action.accept(session))
                .isInstanceOf(ParticipantNotJoinedException.class);
    }

    static Stream<Named<Consumer<RoomSession>>> unknownParticipantActions() {
        return Stream.of(
                Named.of("toggleHandsUp", session -> session.toggleHandsUp("ghost")),
                Named.of("updateMediaOption", session -> session.updateMediaOption("ghost", new MediaOption(true, true))),
                Named.of("addProducer", session -> session.addProducer("ghost", "p1")),
                Named.of("removeProducer", session -> session.removeProducer("ghost", "p1"))
        );
    }

    @Test
    @DisplayName("손들기/미디어/프로듀서 상태를 참여자에 위임한다")
    void delegatesStateToParticipant() {
        RoomSession session = new RoomSession("roomA");
        session.join(participant("u1"));

        assertThat(session.toggleHandsUp("u1")).isTrue();

        MediaOption updated = new MediaOption(false, false);
        session.updateMediaOption("u1", updated);
        session.addProducer("u1", "p1");

        Participant p = session.participants().get(0);
        assertThat(p.isHandsUp()).isTrue();
        assertThat(p.getMediaOption()).isEqualTo(updated);
        assertThat(p.getProducerIds()).containsExactly("p1");

        session.removeProducer("u1", "p1");
        assertThat(session.participants().get(0).getProducerIds()).isEmpty();
    }
}
