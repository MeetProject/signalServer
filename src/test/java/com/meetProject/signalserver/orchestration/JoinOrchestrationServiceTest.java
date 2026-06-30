package com.meetProject.signalserver.orchestration;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.dto.common.MediaOption;
import com.meetProject.signalserver.dto.common.Participant;
import com.meetProject.signalserver.dto.common.User;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.JoinPayload;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.UserService;
import com.meetProject.signalserver.service.message.SignalMessagingService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)

public class JoinOrchestrationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private RoomsService roomsService;

    @Mock
    private SignalMessagingService signalMessagingService;

    @InjectMocks
    private JoinOrchestrationService joinService;

    @Test
    @DisplayName("유저 방 참여 시나리오 테스트")
    void joinUserSuccessfully() {
        String userId = "user1";
        String roomId = "room1";
        User user = new User(userId, "user1", "#000000", null);
        MediaOption mediaOption = new MediaOption(true, true);
        JoinPayload joinPayload = new JoinPayload(roomId, "correlationId", mediaOption, List.of());

        when(userService.getUser(userId)).thenReturn(user);
        when(roomsService.getParticipants(roomId)).thenReturn(List.of());

        joinService.joinRoom(userId, joinPayload);

        verify(roomsService).addParticipant(eq(roomId), any(Participant.class));
        verify(signalMessagingService, times(1)).sendJoin(
                eq(user),
                eq(joinPayload),
                any()
        );
    }

    @Test
    @DisplayName("이미 방에 참여 중인 유저가 다시 참여 시도")
    void joinUserAlreadyInRoom() {
        String userId = "user-123";
        String roomId = "room-abc";
        String correlationId = "corr-789";

        User user = new User(userId, "nickname", "#000000", roomId); // roomId가 이미 할당됨
        when(userService.getUser(userId)).thenReturn(user);

        doThrow(new IllegalArgumentException(ErrorMessage.ROOM_ALREADY_JOINED))
                .when(userService).updateRoomStatus(userId, roomId);

        JoinPayload joinPayload = new JoinPayload(roomId, correlationId, new MediaOption(true, true), List.of());
        joinService.joinRoom(userId, joinPayload);

        verify(signalMessagingService, times(1)).sendError(
                eq(userId),
                argThat(response -> response.message().equals(ErrorMessage.ROOM_ALREADY_JOINED))
        );
    }

    @Test
    @DisplayName("존재하지 않는 방에 참여 시도")
    void joinUserNonExistentRoom() {
        String userId = "user-123";
        String roomId = "room-abc";
        String correlationId = "corr-789";

        when(userService.getUser(userId)).thenReturn(new User(userId, "nick", "#000", null));

        doThrow(new IllegalArgumentException(ErrorMessage.ROOM_NOT_FOUND))
                .when(roomsService).addParticipant(eq(roomId), any());

        JoinPayload joinPayload = new JoinPayload(roomId, correlationId, new MediaOption(true, true), List.of());
        joinService.joinRoom(userId, joinPayload);

        verify(signalMessagingService, times(1)).sendError(
                eq(userId),
                argThat(response -> response.message().equals(ErrorMessage.ROOM_NOT_FOUND))
        );
    }
}