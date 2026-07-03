package com.meetProject.signalserver.domain;

import com.meetProject.signalserver.util.RandomIdGenerator;
import java.time.LocalDateTime;

public class User {
    private final String id;
    private final UserName userName;
    private final ProfileColor profileColor;
    private final LocalDateTime createdAt;

    private User(String id, UserName userName, ProfileColor profileColor, LocalDateTime createdAt) {
        this.id = id;
        this.userName = userName;
        this.profileColor = profileColor;
        this.createdAt = createdAt;
    }

    public static User create(String name, String profileColor) {
        String id = RandomIdGenerator.uuidGenerator();
        return new User(id, new UserName(name), new ProfileColor(profileColor), LocalDateTime.now());
    }

    public static User restore(String id, String name, String profileColor, LocalDateTime createdAt) {
        return new User(id, new UserName(name), new ProfileColor(profileColor), createdAt);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return userName.name();
    }

    public String getProfileColor() {
        return profileColor.value();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
