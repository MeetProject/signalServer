package com.meetProject.signalserver.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import java.security.Principal;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

public class UserIdHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {

        String query = request.getURI().getQuery();
        if (query == null || query.isBlank()) {
            return null;
        }

        List<String> params = Arrays.stream(query.split("&"))
                .filter(q -> q.startsWith("userId="))
                .map(s -> s.substring("userId=".length()))
                .toList();

        if (params.isEmpty()) {
            return null;
        }

        String userId = params.get(0);

        if(userId == null) {
            return null;
        }
        /*
        if (userId.equals("mediaServer")) {
            String Ip = request.getRemoteAddress().getAddress().getHostAddress();
            List<String> allowedIps = List.of("000.000.0.000");
            if (Ip == null || !allowedIps.contains(Ip)) {
                return null;
            }
        }
        */
        attributes.put("userId", userId);

        return () -> userId; // Principal 구현
    }
}