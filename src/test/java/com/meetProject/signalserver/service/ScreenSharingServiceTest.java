package com.meetProject.signalserver.service;

import static org.junit.jupiter.api.Assertions.*;
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
        assertTrue(screenSharingService.isSharing("user1", "room1"));
        assertEquals("user1", screenSharingService.getOwnerId("room1"));

        screenSharingService.stopSharing("room1");
        assertFalse(screenSharingService.isSharing("user1", "room1"));
        assertNull(screenSharingService.getOwnerId("room1"));
    }

    @Test
    @DisplayName("화면 공유를 두 명 이상 할 때")
    void moreThanOneScreenSharing(){
        screenSharingService.startSharing("room1", "user1");
        assertThrows(IllegalArgumentException.class, ()-> screenSharingService.startSharing("room1", "user2"));
    }
}
