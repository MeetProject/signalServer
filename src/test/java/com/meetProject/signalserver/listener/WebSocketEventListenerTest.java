package com.meetProject.signalserver.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.meetProject.signalserver.constant.MediaType;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.MediaLeaveRequest;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.LeaveResponse;
import com.meetProject.signalserver.infrastructure.StompMessageSender;
import com.meetProject.signalserver.service.RoomService;
import com.meetProject.signalserver.service.UserService;
import java.security.Principal;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@ExtendWith(MockitoExtension.class)
public class WebSocketEventListenerTest {
    @Mock RoomService roomService;
    @Mock UserService userService;
    @Mock StompMessageSender stompMessageSender;
    @Mock SimpUserRegistry userRegistry;
    @Mock TaskScheduler taskScheduler;
    @InjectMocks WebSocketEventListener listener;

    private SessionDisconnectEvent event(Principal principal) {
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).build();
        return new SessionDisconnectEvent(this, message, "session-1", CloseStatus.NORMAL, principal);
    }

    private Runnable scheduledTask(String userId) {
        listener.handleWebSocketDisconnect(event(() -> userId));
        ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
        verify(taskScheduler).schedule(captor.capture(), any(Instant.class));
        return captor.getValue();
    }

    @Test
    @DisplayName("principal이 없으면 아무 작업도 예약하지 않는다")
    void skipsWithoutPrincipal() {
        listener.handleWebSocketDisconnect(event(null));

        verifyNoInteractions(taskScheduler);
    }

    @Test
    @DisplayName("유예 후에도 미접속이면 퇴장 알림을 보내고 사용자를 삭제한다")
    void leavesAndDeletesWhenStillDisconnected() {
        given(userRegistry.getUser("u1")).willReturn(null);
        given(roomService.leave("u1")).willReturn(Optional.of("roomA"));

        scheduledTask("u1").run();

        verify(stompMessageSender).broadcast("roomA", TopicType.LEAVE, new LeaveResponse("u1"));
        verify(stompMessageSender).sendToMediaServer(MediaType.LEAVE, new MediaLeaveRequest("roomA", "u1"));
        verify(userService).delete("u1");
    }

    @Test
    @DisplayName("유예 중 재접속하면 아무것도 하지 않는다")
    void skipsWhenReconnected() {
        given(userRegistry.getUser("u1")).willReturn(mock(SimpUser.class));

        scheduledTask("u1").run();

        verifyNoInteractions(roomService, userService, stompMessageSender);
    }

    @Test
    @DisplayName("참여 중인 방이 없어도 사용자는 삭제한다")
    void deletesUserWithoutRoom() {
        given(userRegistry.getUser("u1")).willReturn(null);
        given(roomService.leave("u1")).willReturn(Optional.empty());

        scheduledTask("u1").run();

        verifyNoInteractions(stompMessageSender);
        verify(userService).delete("u1");
    }
}
