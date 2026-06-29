package com.meetProject.signalserver.config;

import jakarta.annotation.Nonnull;
import java.util.Arrays;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Component
public class UserIdHandshakeInterceptor implements HandshakeInterceptor {

    private static final String USER_ID_PARAM = "userId=";
    static final String USER_ID_ATTRIBUTE = "userId";

    @Override
    public boolean beforeHandshake(@Nonnull ServerHttpRequest request,
                                   @Nonnull ServerHttpResponse response,
                                   @Nonnull WebSocketHandler wsHandler,
                                   @Nonnull Map<String, Object> attributes) {
        String userId = extractUserId(request.getURI().getQuery());
        if (userId == null || userId.isBlank()) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }
        attributes.put(USER_ID_ATTRIBUTE, userId);
        return true;
    }

    @Override
    public void afterHandshake(@Nonnull ServerHttpRequest request,
                               @Nonnull ServerHttpResponse response,
                               @Nonnull WebSocketHandler wsHandler,
                               Exception exception) {
    }

    private String extractUserId(String query) {
        if (query == null || query.isBlank()) {
            return null;
        }
        return Arrays.stream(query.split("&"))
                .filter(param -> param.startsWith(USER_ID_PARAM))
                .map(param -> param.substring(USER_ID_PARAM.length()))
                .findFirst()
                .orElse(null);
    }
}
