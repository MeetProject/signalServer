package com.meetProject.signalserver.service;

import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.model.ScreenSharing;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class ScreenSharingService {
    private final Map<String, ScreenSharing> activeScreenSharings =  new ConcurrentHashMap<>();

    public void startSharing(String roomId, String ownerId) {
        if(activeScreenSharings.containsKey(roomId)) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_ALREADY_SCREEN_SHARING);
        }
        activeScreenSharings.put(roomId, new ScreenSharing(roomId, ownerId));
    }

    public void stopSharing(String roomId) {
        activeScreenSharings.remove(roomId);
    }


    public String getOwnerId(String roomId) {
        ScreenSharing screenSharing = activeScreenSharings.get(roomId);
        if(screenSharing == null) {
            return null;
        }
        return screenSharing.ownerId();
    }

    public boolean isSharing(String userId, String roomId) {
        return activeScreenSharings.containsKey(roomId) && activeScreenSharings.get(roomId).ownerId().equals(userId);
    }
}
