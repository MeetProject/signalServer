package com.meetProject.signalserver.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.meetProject.signalserver.constant.RoomRule;
import com.meetProject.signalserver.domain.MediaOption;
import com.meetProject.signalserver.domain.Participant;
import com.meetProject.signalserver.domain.Room;
import com.meetProject.signalserver.domain.User;
import com.meetProject.signalserver.dto.application.JoinResult;
import com.meetProject.signalserver.dto.application.ResyncResult;
import com.meetProject.signalserver.exception.RoomFullException;
import com.meetProject.signalserver.exception.RoomNotFoundException;
import com.meetProject.signalserver.exception.UserNotFoundException;
import com.meetProject.signalserver.fake.FakeRoomRepository;
import com.meetProject.signalserver.fake.FakeUserRepository;
import com.meetProject.signalserver.repository.RoomSessionRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RoomServiceTest {
    private final FakeRoomRepository roomRepository = new FakeRoomRepository();
    private final FakeUserRepository userRepository = new FakeUserRepository();
    private final RoomSessionRepository roomSessionRepository = new RoomSessionRepository();
    private final RoomService roomService =
            new RoomService(roomRepository, userRepository, roomSessionRepository);

    private final MediaOption mediaOption = new MediaOption(true, true);

    private void saveUser(String id) {
        userRepository.save(User.restore(id, "name", "#ffffff", LocalDateTime.now()));
    }

    private void saveRoom(String id) {
        roomRepository.save(Room.restore(id, LocalDateTime.now()));
    }

    private Participant participant(String userId) {
        return Participant.join(User.restore(userId, "name", "#ffffff", LocalDateTime.now()), mediaOption);
    }

    @Test
    @DisplayName("방을 생성하고 저장한다")
    void savesRoom() {
        String roomId = roomService.save();

        assertThat(roomId).hasSize(RoomRule.ROOM_ID_LENGTH);
        assertThat(roomRepository.existsById(roomId)).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 방은 검증 실패")
    void validateFailsWhenMissing() {
        assertThat(roomService.validate("nope")).isFalse();
    }

    @Test
    @DisplayName("존재하고 세션이 없으면 검증 통과")
    void validatePassesWhenNoSession() {
        saveRoom("roomA");

        assertThat(roomService.validate("roomA")).isTrue();
    }

    @Test
    @DisplayName("세션에 여유가 있으면 검증 통과")
    void validatePassesWhenSessionHasCapacity() {
        saveRoom("roomA");
        roomSessionRepository.join("roomA", participant("u1"));

        assertThat(roomService.validate("roomA")).isTrue();
    }

    @Test
    @DisplayName("정원이 가득 차면 검증 실패")
    void validateFailsWhenFull() {
        saveRoom("roomA");
        for (int i = 0; i < RoomRule.MAX_ROOM_PARTICIPANTS; i++) {
            roomSessionRepository.join("roomA", participant("u" + i));
        }

        assertThat(roomService.validate("roomA")).isFalse();
    }

    @Test
    @DisplayName("입장 성공 시 참여자 결과를 반환한다")
    void joinsSuccessfully() {
        saveUser("u1");
        saveRoom("roomA");

        JoinResult result = roomService.join("u1", "roomA", mediaOption);

        assertThat(result.joiner().getUserId()).isEqualTo("u1");
        assertThat(result.others()).isEmpty();
        assertThat(roomSessionRepository.findByUserId("u1")).isPresent();
    }

    @Test
    @DisplayName("사용자가 없으면 입장 실패")
    void joinFailsWhenUserMissing() {
        saveRoom("roomA");

        assertThatThrownBy(() -> roomService.join("ghost", "roomA", mediaOption))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    @DisplayName("방이 없으면 입장 실패")
    void joinFailsWhenRoomMissing() {
        saveUser("u1");

        assertThatThrownBy(() -> roomService.join("u1", "nope", mediaOption))
                .isInstanceOf(RoomNotFoundException.class);
    }

    @Test
    @DisplayName("정원이 가득 찬 방 입장은 실패")
    void joinFailsWhenRoomFull() {
        saveUser("u1");
        saveRoom("roomA");
        for (int i = 0; i < RoomRule.MAX_ROOM_PARTICIPANTS; i++) {
            roomSessionRepository.join("roomA", participant("f" + i));
        }

        assertThatThrownBy(() -> roomService.join("u1", "roomA", mediaOption))
                .isInstanceOf(RoomFullException.class);
    }

    @Test
    @DisplayName("resync 시 세션에 있으면 참여자 목록을 반환한다")
    void resyncReturnsParticipants() {
        roomSessionRepository.join("roomA", participant("u1"));

        ResyncResult result = roomService.resync("u1", "roomA");

        assertThat(result.rejoinRequired()).isFalse();
        assertThat(result.participants()).hasSize(1);
    }

    @Test
    @DisplayName("resync 시 세션이 없으면 재입장 필요로 보고한다")
    void resyncRequiresRejoin() {
        ResyncResult result = roomService.resync("ghost", "roomA");

        assertThat(result.rejoinRequired()).isTrue();
        assertThat(result.participants()).isEmpty();
    }

    @Test
    @DisplayName("resync 시 요청한 방과 세션의 방이 다르면 재입장 필요로 보고한다")
    void resyncRequiresRejoinWhenRoomMismatched() {
        roomSessionRepository.join("roomA", participant("u1"));

        ResyncResult result = roomService.resync("u1", "roomB");

        assertThat(result.rejoinRequired()).isTrue();
        assertThat(result.participants()).isEmpty();
    }

    @Test
    @DisplayName("마지막 참여자 퇴장 시 방을 삭제한다")
    void leaveDeletesEmptyRoom() {
        saveRoom("roomA");
        roomSessionRepository.join("roomA", participant("u1"));

        Optional<String> roomId = roomService.leave("u1");

        assertThat(roomId).contains("roomA");
        assertThat(roomRepository.existsById("roomA")).isFalse();
    }

    @Test
    @DisplayName("다른 참여자가 남으면 방을 삭제하지 않는다")
    void leaveKeepsNonEmptyRoom() {
        saveRoom("roomA");
        roomSessionRepository.join("roomA", participant("u1"));
        roomSessionRepository.join("roomA", participant("u2"));

        Optional<String> roomId = roomService.leave("u1");

        assertThat(roomId).contains("roomA");
        assertThat(roomRepository.existsById("roomA")).isTrue();
    }

    @Test
    @DisplayName("참여 중인 방이 없으면 빈 결과")
    void leaveReturnsEmptyWhenNotJoined() {
        assertThat(roomService.leave("ghost")).isEmpty();
    }
}
