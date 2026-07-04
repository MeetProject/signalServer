package com.meetProject.signalserver.config;

import jakarta.annotation.Nonnull;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriUtils;

@Component
public class UserIdHandshakeInterceptor implements HandshakeInterceptor {

    private static final String USER_ID_PARAM = "userId";
    private static final String TOKEN_PARAM = "token";
    static final String USER_ID_ATTRIBUTE = "userId";

    private final MediaServerProperties mediaServerProperties;

    public UserIdHandshakeInterceptor(MediaServerProperties mediaServerProperties) {
        this.mediaServerProperties = mediaServerProperties;
    }

    @Override
    public boolean beforeHandshake(@Nonnull ServerHttpRequest request,
                                   @Nonnull ServerHttpResponse response,
                                   @Nonnull WebSocketHandler wsHandler,
                                   @Nonnull Map<String, Object> attributes) {
        String query = request.getURI().getRawQuery();
        String userId = extractParam(query, USER_ID_PARAM);
        if (userId == null || userId.isBlank()) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }
        if (mediaServerProperties.id().equals(userId) && !isValidMediaServerToken(extractParam(query, TOKEN_PARAM))) {
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

    private boolean isValidMediaServerToken(String token) {
        if (token == null) {
            return false;
        }
        return MessageDigest.isEqual(
                token.getBytes(StandardCharsets.UTF_8),
                mediaServerProperties.token().getBytes(StandardCharsets.UTF_8)
        );
    }

    private String extractParam(String query, String name) {
        if (query == null || query.isBlank()) {
            return null;
        }
        String prefix = name + "=";
        return Arrays.stream(query.split("&"))
                .filter(param -> param.startsWith(prefix))
                .findFirst()
                .map(param -> decode(param.substring(prefix.length())))
                .orElse(null);
    }

    private String decode(String value) {
        try {
            return UriUtils.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
