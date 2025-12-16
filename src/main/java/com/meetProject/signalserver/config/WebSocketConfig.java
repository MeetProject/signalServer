package com.meetProject.signalserver.config;

import com.meetProject.signalserver.handler.UnifiedWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final UnifiedWebSocketHandler unifiedWebSocketHandler;
    private final UserIdHandshakeHandler userIdHandshakeHandler;

    @Autowired
    public WebSocketConfig(UnifiedWebSocketHandler unifiedWebSocketHandler, UserIdHandshakeHandler userIdHandshakeHandler) {
        this.unifiedWebSocketHandler = unifiedWebSocketHandler;
        this.userIdHandshakeHandler = userIdHandshakeHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(unifiedWebSocketHandler, "/ws")
                .setHandshakeHandler(userIdHandshakeHandler)
                .setAllowedOriginPatterns("*");

    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container =
                new ServletServerContainerFactoryBean();

        container.setMaxTextMessageBufferSize(1024 * 1024 * 100);
        container.setMaxBinaryMessageBufferSize(1024 * 1024 * 100);

        return container;
    }
}