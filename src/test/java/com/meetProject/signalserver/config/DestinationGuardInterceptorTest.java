package com.meetProject.signalserver.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.security.Principal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;

public class DestinationGuardInterceptorTest {
    private final DestinationGuardInterceptor interceptor =
            new DestinationGuardInterceptor(new MediaServerProperties("mediaServer", "secret-token"));
    private final MessageChannel channel = mock(MessageChannel.class);

    private Message<byte[]> stompMessage(StompCommand command, String destination) {
        return stompMessage(command, destination, null);
    }

    private Message<byte[]> stompMessage(StompCommand command, String destination, Principal principal) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(command);
        if (destination != null) {
            accessor.setDestination(destination);
        }
        if (principal != null) {
            accessor.setUser(principal);
        }
        return MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders());
    }

    @Test
    @DisplayName("/app 경로 SEND는 통과한다")
    void allowsAppDestination() {
        Message<byte[]> message = stompMessage(StompCommand.SEND, "/app/chat/send");

        assertThat(interceptor.preSend(message, channel)).isSameAs(message);
    }

    @Test
    @DisplayName("/app 외 경로 SEND는 예외")
    void rejectsOtherDestination() {
        Message<byte[]> message = stompMessage(StompCommand.SEND, "/topic/room/roomA/chat");

        assertThatThrownBy(() -> interceptor.preSend(message, channel))
                .isInstanceOf(MessagingException.class);
    }

    @Test
    @DisplayName("목적지 없는 SEND는 예외")
    void rejectsMissingDestination() {
        Message<byte[]> message = stompMessage(StompCommand.SEND, null);

        assertThatThrownBy(() -> interceptor.preSend(message, channel))
                .isInstanceOf(MessagingException.class);
    }

    @Test
    @DisplayName("SEND가 아닌 명령은 검사하지 않는다")
    void ignoresNonSendCommand() {
        Message<byte[]> message = stompMessage(StompCommand.SUBSCRIBE, "/topic/room/roomA/chat");

        assertThat(interceptor.preSend(message, channel)).isSameAs(message);
    }

    @Test
    @DisplayName("미디어 서버는 /app/media 경로로 보낼 수 있다")
    void allowsMediaDestinationForMediaServer() {
        Message<byte[]> message = stompMessage(StompCommand.SEND, "/app/media/rtls", () -> "mediaServer");

        assertThat(interceptor.preSend(message, channel)).isSameAs(message);
    }

    @Test
    @DisplayName("일반 사용자의 /app/media SEND는 예외")
    void rejectsMediaDestinationForUser() {
        Message<byte[]> message = stompMessage(StompCommand.SEND, "/app/media/rtls", () -> "u1");

        assertThatThrownBy(() -> interceptor.preSend(message, channel))
                .isInstanceOf(MessagingException.class);
    }

    @Test
    @DisplayName("principal 없는 /app/media SEND는 예외")
    void rejectsMediaDestinationWithoutPrincipal() {
        Message<byte[]> message = stompMessage(StompCommand.SEND, "/app/media/rtls");

        assertThatThrownBy(() -> interceptor.preSend(message, channel))
                .isInstanceOf(MessagingException.class);
    }
}
