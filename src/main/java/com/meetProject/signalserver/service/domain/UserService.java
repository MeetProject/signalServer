package com.meetProject.signalserver.service.domain;

import com.meetProject.signalserver.model.User;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public void addUser(User user) {
        users.put(user.userId(), user);
    }

    public void removeUser(String id) {
        users.remove(id);
    }

    public User getUser(String id) {
        return users.get(id);
    }

    public String getRoomId(String id) {
        User user = users.get(id);
        if(user == null){
            return null;
        }
        return user.roomId();
    }

    public void updateRoomStatus(String id, String roomId) {
        User user = users.get(id);
        if(user == null){
            return;
        }
        User updated = new User(user.userId(), user.userName(), user.profileColor(), roomId, user.isHandUp());
        users.put(id, updated);
    }

    public void updateUserHandUpStatus(String id, boolean isHandUp) {
        User user = users.get(id);
        if(user == null){
            return;
        }
        User updated = new User(user.userId(), user.userName(), user.profileColor(), user.roomId(), isHandUp);
        users.put(id, updated);
    }
}
