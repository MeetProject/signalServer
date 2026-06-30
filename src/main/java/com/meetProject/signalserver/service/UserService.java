package com.meetProject.signalserver.service;

import com.meetProject.signalserver.domain.User;
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

}
