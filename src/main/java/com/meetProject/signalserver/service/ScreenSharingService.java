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

    public boolean isSharingActive(String roomId) {
        return activeScreenSharings.containsKey(roomId);
    }

    public ScreenSharing getScreenSharing(String roomId) {
        return activeScreenSharings.get(roomId);
    }

    public Boolean isScreenSharingId(String ownerId) {
        activeScreenSharings.forEach((key,value)->{System.out.println(key + ": " + value);});
        return activeScreenSharings.values().stream().anyMatch(screen -> screen.ownerId().equals(ownerId));
    }
}
