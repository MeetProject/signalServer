package com.meetProject.signalserver.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;

public class UserIdHandshakeInterceptorTest {
    private final UserIdHandshakeInterceptor interceptor =
            new UserIdHandshakeInterceptor(new MediaServerProperties("mediaServer", "secret-token"));
    private final ServerHttpRequest request = mock(ServerHttpRequest.class);
    private final ServerHttpResponse response = mock(ServerHttpResponse.class);
    private final WebSocketHandler wsHandler = mock(WebSocketHandler.class);

    private boolean handshake(String uri, Map<String, Object> attributes) {
        given(request.getURI()).willReturn(URI.create(uri));
        return interceptor.beforeHandshake(request, response, wsHandler, attributes);
    }

    @Test
    @DisplayName("userId 파라미터가 있으면 통과하고 속성에 저장한다")
    void acceptsWithUserId() {
        Map<String, Object> attributes = new HashMap<>();

        assertThat(handshake("ws://localhost/ws?userId=u1", attributes)).isTrue();
        assertThat(attributes).containsEntry("userId", "u1");
    }

    @Test
    @DisplayName("다른 파라미터 사이에서도 userId를 추출한다")
    void extractsAmongOtherParams() {
        Map<String, Object> attributes = new HashMap<>();

        assertThat(handshake("ws://localhost/ws?a=b&userId=u2&c=d", attributes)).isTrue();
        assertThat(attributes).containsEntry("userId", "u2");
    }

    @Test
    @DisplayName("userId가 없으면 403으로 거절한다")
    void rejectsWithoutUserId() {
        assertThat(handshake("ws://localhost/ws", new HashMap<>())).isFalse();
        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("userId가 비어 있으면 403으로 거절한다")
    void rejectsBlankUserId() {
        assertThat(handshake("ws://localhost/ws?userId=", new HashMap<>())).isFalse();
        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("미디어 서버 id는 올바른 토큰이면 통과한다")
    void acceptsMediaServerWithValidToken() {
        Map<String, Object> attributes = new HashMap<>();

        assertThat(handshake("ws://localhost/ws?userId=mediaServer&token=secret-token", attributes)).isTrue();
        assertThat(attributes).containsEntry("userId", "mediaServer");
    }

    @Test
    @DisplayName("미디어 서버 id에 토큰이 틀리면 403으로 거절한다")
    void rejectsMediaServerWithWrongToken() {
        assertThat(handshake("ws://localhost/ws?userId=mediaServer&token=wrong", new HashMap<>())).isFalse();
        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("미디어 서버 id에 토큰이 없으면 403으로 거절한다")
    void rejectsMediaServerWithoutToken() {
        assertThat(handshake("ws://localhost/ws?userId=mediaServer", new HashMap<>())).isFalse();
        verify(response).setStatusCode(HttpStatus.FORBIDDEN);
    }
}
