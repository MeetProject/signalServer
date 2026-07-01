package com.meetProject.signalserver.domain;

import com.meetProject.signalserver.exception.InvalidInputException;
import com.meetProject.signalserver.util.RandomIdGenerator;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class User {
    private static final Pattern HEX_COLOR_PATTERN =
            Pattern.compile("^#([0-9a-fA-F]{3}|[0-9a-fA-F]{4}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8})$");

    private final String id;
    private final UserName userName;
    private final String profileColor;
    private final LocalDateTime createdAt;

    private User(String id, UserName userName, String profileColor, LocalDateTime createdAt) {
        this.id = id;
        this.userName = userName;
        this.profileColor = profileColor;
        this.createdAt = createdAt;
    }

    public static User create(String name, String profileColor) {
        String id = RandomIdGenerator.uuidGenerator();
        UserName userName = new UserName(name);
        validateColor(profileColor);
        return new User(id, userName, profileColor, LocalDateTime.now());
    }

    public static User restore(String id, String name, String profileColor, LocalDateTime createdAt) {
        UserName userName = new UserName(name);
        return new User(id, userName, profileColor, createdAt);
    }

    public String getId() {
        return id;
    }

    public UserName getUserName() {
        return userName;
    }

    public String getProfileColor() {
        return profileColor;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    private static void validateColor(String color) {
        if(!HEX_COLOR_PATTERN.matcher(color).matches()) {
            throw new InvalidInputException("유효하지 않은 프로필 색상입니다.");
        }
    }
}
