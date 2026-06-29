package com.meetProject.signalserver.config;

import jakarta.annotation.Nonnull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class DestinationGuardInterceptor implements ChannelInterceptor {
    private static final String ALLOWED_SEND_PREFIX = "/app/";

    @Override
    public Message<?> preSend(@Nonnull Message<?> message, @Nonnull MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || !StompCommand.SEND.equals(accessor.getCommand())) {
            return message;
        }

        String destination = accessor.getDestination();
        if (destination == null || !destination.startsWith(ALLOWED_SEND_PREFIX)) {
            throw new MessagingException("Forbidden SEND destination: " + destination);
        }

        return message;
    }
}
