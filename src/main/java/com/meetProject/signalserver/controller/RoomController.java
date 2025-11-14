package com.meetProject.signalserver.controller;

import com.meetProject.signalserver.constant.RoomRule;
import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.constant.StreamType;
import com.meetProject.signalserver.model.Room;
import com.meetProject.signalserver.model.dto.CreateRoomResponse;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.ScreenSharingService;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.service.UserService;
import com.meetProject.signalserver.util.RandomIdGenerator;
import java.util.ArrayList;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoomController {
    private final RoomsService roomsService;
    private final ScreenSharingService screenSharingService;
    private final SignalMessagingService signalMessagingService;
    private final UserService userService;

    public RoomController(RoomsService roomsService, ScreenSharingService screenSharingService, SignalMessagingService signalMessagingService, UserService userService) {
        this.roomsService = roomsService;
        this.screenSharingService = screenSharingService;
        this.signalMessagingService = signalMessagingService;
        this.userService = userService;
    }

    @PostMapping("/api/room/create")
    public CreateRoomResponse createRoom() {
        String roomId = RandomIdGenerator.randomId(RoomRule.ROOM_ID_LENGTH);
        Room room = new Room(roomId, new ArrayList<>());
        roomsService.createRoom(room);
        return new CreateRoomResponse(SignalType.CREATE,roomId);
    }

    @PostMapping("/api/leave")
    public void forceLeave(@RequestParam String userId, @RequestParam String roomId) {
        if(screenSharingService.isSharing(userId)) {
            screenSharingService.stopSharing(roomId);
            signalMessagingService.sendLeave(roomId, userId, StreamType.SCREEN);
        }
        roomsService.removeParticipant(roomId, userId);
        userService.updateRoomStatus(userId, roomId);
        signalMessagingService.sendLeave(roomId, userId, StreamType.USER);
    }
}
