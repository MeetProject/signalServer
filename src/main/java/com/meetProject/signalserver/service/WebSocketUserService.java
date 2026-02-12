package com.meetProject.signalserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
public class WebSocketUserService {

    @Autowired
    private SimpUserRegistry userRegistry;

    public boolean isUserConnected(String userId) {
        return userRegistry.getUser(userId) != null;
    }
}