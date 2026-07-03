package com.meetProject.signalserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final UserIdHandshakeHandler userIdHandshakeHandler;
    private final UserIdHandshakeInterceptor userIdHandshakeInterceptor;
    private final DestinationGuardInterceptor destinationGuardInterceptor;

    public WebSocketConfig(UserIdHandshakeHandler userIdHandshakeHandler,
                           UserIdHandshakeInterceptor userIdHandshakeInterceptor,
                           DestinationGuardInterceptor destinationGuardInterceptor) {
        this.userIdHandshakeHandler = userIdHandshakeHandler;
        this.userIdHandshakeInterceptor = userIdHandshakeInterceptor;
        this.destinationGuardInterceptor = destinationGuardInterceptor;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setHandshakeHandler(userIdHandshakeHandler)
                .addInterceptors(userIdHandshakeInterceptor)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue", "/media");
        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(destinationGuardInterceptor);
    }
}