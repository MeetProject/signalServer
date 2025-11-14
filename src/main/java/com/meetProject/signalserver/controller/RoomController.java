package com.meetProject.signalserver.controller;

import com.meetProject.signalserver.constant.RoomRule;
import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.constant.StreamType;
import com.meetProject.signalserver.model.Room;
import com.meetProject.signalserver.model.dto.CreateRoomResponse;
import com.meetProject.signalserver.service.RoomsManagementService;
import com.meetProject.signalserver.service.ScreenSharingService;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.service.UserManagementService;
import com.meetProject.signalserver.util.RandomIdGenerator;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoomController {
    private final RoomsManagementService roomsManagementService;
    private final ScreenSharingService screenSharingService;
    private final SignalMessagingService signalMessagingService;
    private final UserManagementService userManagementService;

    public RoomController(RoomsManagementService roomsManagementService, ScreenSharingService screenSharingService, SignalMessagingService signalMessagingService, UserManagementService userManagementService) {
        this.roomsManagementService = roomsManagementService;
        this.screenSharingService = screenSharingService;
        this.signalMessagingService = signalMessagingService;
        this.userManagementService = userManagementService;
    }

    @PostMapping("/create")
    public CreateRoomResponse createRoom() {
        String roomId = RandomIdGenerator.randomId(RoomRule.ROOM_ID_LENGTH);
        Room room = new Room(roomId, new ArrayList<>());
        roomsManagementService.createRoom(room);
        return new CreateRoomResponse(SignalType.CREATE,roomId);
    }

    @PostMapping("/api/leave")
    public void forceLeave(@RequestParam String userId, @RequestParam String roomId) {
        if(screenSharingService.isScreenSharingId(userId)) {
            screenSharingService.stopSharing(roomId);
            signalMessagingService.sendLeave(roomId, userId, StreamType.SCREEN);
        }
        roomsManagementService.removeParticipant(roomId, userId);
        userManagementService.updateRoomStatus(userId, roomId);
        signalMessagingService.sendLeave(roomId, userId, StreamType.USER);
    }
}
