package com.meetProject.signalserver.service;

import com.meetProject.signalserver.model.User;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserManagementService {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public void addUser(String id, User user) {
        users.put(id, user);
    }

    public void removeUser(String id) {
        users.remove(id);
    }

    public User getUser(String id) {
        return users.get(id);
    }
}
