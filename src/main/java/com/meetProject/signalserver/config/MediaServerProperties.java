package com.meetProject.signalserver.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "signal.media-server")
public record MediaServerProperties(String id, String token) {
}
