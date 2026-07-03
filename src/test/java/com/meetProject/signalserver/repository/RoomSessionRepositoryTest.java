package com.meetProject.signalserver.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.meetProject.signalserver.constant.RoomRule;
import com.meetProject.signalserver.domain.MediaOption;
import com.meetProject.signalserver.domain.Participant;
import com.meetProject.signalserver.domain.User;
import com.meetProject.signalserver.exception.RoomFullException;
import com.meetProject.signalserver.repository.RoomSessionRepository.LeaveResult;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RoomSessionRepositoryTest {
    private final RoomSessionRepository repository = new RoomSessionRepository();
    private final MediaOption mediaOption = new MediaOption(true, true);

    private Participant participant(String userId) {
        return Participant.join(User.restore(userId, "name", "#ffffff", LocalDateTime.now()), mediaOption);
    }

    @Test
    @DisplayName("첫 참여는 세션을 만들고 빈 기존목록을 반환한다")
    void joinCreatesSession() {
        var others = repository.join("roomA", participant("u1"));

        assertThat(others).isEmpty();
        assertThat(repository.find("roomA")).isPresent();
        assertThat(repository.findRoomIdByUserId("u1")).contains("roomA");
    }

    @Test
    @DisplayName("같은 방 다음 참여는 기존 참여자를 반환한다")
    void joinReturnsExisting() {
        repository.join("roomA", participant("u1"));

        var others = repository.join("roomA", participant("u2"));

        assertThat(others).hasSize(1);
        assertThat(repository.findByUserId("u2")).isPresent();
        assertThat(repository.find("roomA").orElseThrow().participants()).hasSize(2);
    }

    @Test
    @DisplayName("마지막 참여자 퇴장 시 세션이 제거되고 빈방으로 보고된다")
    void leaveLastRemovesSession() {
        repository.join("roomA", participant("u1"));

        Optional<LeaveResult> result = repository.leave("u1");

        assertThat(result).contains(new LeaveResult("roomA", true));
        assertThat(repository.find("roomA")).isEmpty();
        assertThat(repository.findRoomIdByUserId("u1")).isEmpty();
    }

    @Test
    @DisplayName("일부 퇴장은 세션을 유지하고 빈방이 아니다")
    void leaveKeepsSessionWhenOthersRemain() {
        repository.join("roomA", participant("u1"));
        repository.join("roomA", participant("u2"));

        Optional<LeaveResult> result = repository.leave("u1");

        assertThat(result).contains(new LeaveResult("roomA", false));
        assertThat(repository.find("roomA")).isPresent();
    }

    @Test
    @DisplayName("참여하지 않은 사용자 퇴장은 빈 결과")
    void leaveUnknownUserReturnsEmpty() {
        assertThat(repository.leave("ghost")).isEmpty();
    }

    @Test
    @DisplayName("만원 방 입장 실패 시 사용자-방 매핑이 남지 않는다")
    void fullRoomJoinLeavesNoMapping() {
        for (int i = 0; i < RoomRule.MAX_ROOM_PARTICIPANTS; i++) {
            repository.join("roomA", participant("u" + i));
        }

        assertThatThrownBy(() -> repository.join("roomA", participant("overflow")))
                .isInstanceOf(RoomFullException.class);
        assertThat(repository.findRoomIdByUserId("overflow")).isEmpty();
    }

    @Test
    @DisplayName("다른 방으로 입장하면 최신 방이 조회된다")
    void rejoinUpdatesMapping() {
        repository.join("roomA", participant("u1"));

        repository.join("roomB", participant("u1"));

        assertThat(repository.findRoomIdByUserId("u1")).contains("roomB");
    }

    @Test
    @DisplayName("없는 방·사용자 조회는 빈 결과")
    void findMissingReturnsEmpty() {
        assertThat(repository.find("nope")).isEmpty();
        assertThat(repository.findRoomIdByUserId("ghost")).isEmpty();
        assertThat(repository.findByUserId("ghost")).isEmpty();
    }
}
