package com.meetProject.signalserver.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.security.Principal;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;

public class UserIdHandshakeHandlerTest {
    private final UserIdHandshakeHandler handler = new UserIdHandshakeHandler();
    private final ServerHttpRequest request = mock(ServerHttpRequest.class);
    private final WebSocketHandler wsHandler = mock(WebSocketHandler.class);

    @Test
    @DisplayName("속성의 userId로 Principal을 만든다")
    void createsPrincipalFromAttribute() {
        Principal principal = handler.determineUser(request, wsHandler, Map.<String, Object>of("userId", "u1"));

        assertThat(principal).isNotNull();
        assertThat(principal.getName()).isEqualTo("u1");
    }

    @Test
    @DisplayName("userId가 없으면 Principal이 없다")
    void returnsNullWithoutUserId() {
        assertThat(handler.determineUser(request, wsHandler, Map.of())).isNull();
    }
}
