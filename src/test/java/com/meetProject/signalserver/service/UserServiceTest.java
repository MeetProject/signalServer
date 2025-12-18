package com.meetProject.signalserver.service;

import com.meetProject.signalserver.model.User;
import com.meetProject.signalserver.service.domain.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class UserServiceTest {
    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    @DisplayName("사용자 추가")
    void addUser() {
        User user = new User("userId", "user", "#000000", "room1", false);
        userService.addUser(user);

        User u = userService.getUser("userId");
        assertThat(u).isNotNull();
        assertThat(u.userId()).isEqualTo("userId");
        assertThat(u.roomId()).isEqualTo("room1");

    }

    @Test
    @DisplayName("사용자 삭제")
    void removeUser() {
        User user = new User("user1", "#000000", "userId", "room1", false);
        userService.addUser(user);
        userService.removeUser("userId");

        assertThat(userService.getUser("userId")).isNull();
    }

    @Test
    @DisplayName("사용자 방 변경")
    void updateUserRoomStatus() {
        User user = new User("userId", "user", "#000000", "room1", false);
        userService.addUser(user);

        userService.updateRoomStatus("userId", "room2");
        assertThat(userService.getUser("userId").roomId()).isEqualTo("room2");
    }
}
