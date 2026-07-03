package com.meetProject.signalserver.config;

import jakarta.annotation.Nonnull;
import java.security.Principal;
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
    private static final String MEDIA_SEND_PREFIX = "/app/media/";

    private final MediaServerProperties mediaServerProperties;

    public DestinationGuardInterceptor(MediaServerProperties mediaServerProperties) {
        this.mediaServerProperties = mediaServerProperties;
    }

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

        if (destination.startsWith(MEDIA_SEND_PREFIX) && !isMediaServer(accessor.getUser())) {
            throw new MessagingException("Forbidden media SEND destination: " + destination);
        }

        return message;
    }

    private boolean isMediaServer(Principal principal) {
        return principal != null && mediaServerProperties.id().equals(principal.getName());
    }
}
