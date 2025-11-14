package com.meetProject.signalserver.model;

import java.util.UUID;

public record ScreenSharing(String id, String roomId, String ownerId) {
    public ScreenSharing(String roomId, String ownerId) {
        this(UUID.randomUUID().toString(), roomId, ownerId);
    }
}
