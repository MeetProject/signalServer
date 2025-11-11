package com.meetProject.signalserver.controller;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.model.Room;
import com.meetProject.signalserver.model.dto.CreateRoomResponse;
import com.meetProject.signalserver.service.RoomsManagementService;
import java.util.ArrayList;
import java.util.UUID;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/room")
public class RoomCreateController {
    private final RoomsManagementService roomsManagementService;
    public RoomCreateController(RoomsManagementService roomsManagementService) {
        this.roomsManagementService = roomsManagementService;
    }

    @PostMapping("/create")
    public CreateRoomResponse createRoom() {
        String roomId = UUID.randomUUID().toString();
        Room room = new Room(roomId, new ArrayList<>());
        roomsManagementService.createRoom(room);
        return new CreateRoomResponse(SignalType.CREATE,roomId);
    }
}
