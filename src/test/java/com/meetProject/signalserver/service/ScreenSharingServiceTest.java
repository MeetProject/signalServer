package com.meetProject.signalserver.service;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ScreenSharingServiceTest {
    private ScreenSharingService screenSharingService;

    @BeforeEach
    public void setup(){
        screenSharingService = new ScreenSharingService();
    }

    @Test
    @DisplayName("화면 공유 테스트")
    void startAndStopScreenSharing(){
        screenSharingService.startSharing("room1", "user1");
        assertThat(screenSharingService.isSharing("user1", "room1")).isTrue();
        assertThat(screenSharingService.getOwnerId("room1")).isEqualTo("user1");

        screenSharingService.stopSharing("room1");
        assertThat(screenSharingService.isSharing("user1", "room1")).isFalse();
        assertThat(screenSharingService.getOwnerId("room1")).isNull();
    }

    @Test
    @DisplayName("화면 공유를 두 명 이상 할 때")
    void moreThanOneScreenSharing(){
        screenSharingService.startSharing("room1", "user1");
        assertThatThrownBy(()-> screenSharingService.startSharing("room1", "user2")).isInstanceOf(IllegalArgumentException.class);
    }
}
