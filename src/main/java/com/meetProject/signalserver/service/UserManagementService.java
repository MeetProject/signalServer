package com.meetProject.signalserver.service;

import com.meetProject.signalserver.model.User;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class UserManagementService {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public void addUser(String id, User user) {
        users.put(id, user);
    }

    public void removeUser(String id) {
        users.remove(id);
    }

    public boolean isUserNotExist(String id) {
        return !users.containsKey(id);
    }

    public User getUser(String id) {
        return users.get(id);
    }

    public String getUserRoomStatus(String id) {
        User user = users.get(id);
        if (user == null) {
            return null;
        }
        return user.roomId();
    }

    public void updateRoomStatus(String id, String roomId) {
        User user = users.get(id);
        User updated = new User( user.userName(), user.profileColor(), user.userId(), roomId);
        users.put(id, updated);
    }
}
