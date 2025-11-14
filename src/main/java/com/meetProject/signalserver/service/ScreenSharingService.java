package com.meetProject.signalserver.service;

import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.model.ScreenSharing;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class ScreenSharingService {
    private final Map<String, ScreenSharing> activeScreenSharings =  new ConcurrentHashMap<>();

    public void startSharing(String roomId, ScreenSharing screenSharing) {
        if(activeScreenSharings.containsKey(roomId)) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_ALREADY_SCREEN_SHARING);
        }
        activeScreenSharings.put(roomId, screenSharing);
    }

    public void stopSharing(String roomId) {
        activeScreenSharings.remove(roomId);
    }


    public String getScreenSharingOwnerId(String roomId) {
        ScreenSharing screenSharing = activeScreenSharings.get(roomId);
        if(screenSharing == null) {
            return null;
        }
        return screenSharing.ownerId();
    }

    public boolean isScreenSharingId(String ownerId) {
        return activeScreenSharings.values().stream()
                .anyMatch(screen -> screen.ownerId().equals(ownerId));
    }
}
