package com.meetProject.signalserver.controller.rest;

import com.meetProject.signalserver.constant.RoomRule;
import com.meetProject.signalserver.model.Room;
import com.meetProject.signalserver.model.dto.CreateRoomResponse;
import com.meetProject.signalserver.model.dto.ValidateRoomResponse;
import com.meetProject.signalserver.orchestration.LeaveOrchestrationService;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.util.RandomIdGenerator;
import java.util.Collections;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomsService roomsService;
    private final LeaveOrchestrationService leaveService;

    public RoomController(RoomsService roomsService, LeaveOrchestrationService leaveService) {
        this.roomsService = roomsService;
        this.leaveService = leaveService;

    }

    @PostMapping
    public ResponseEntity<CreateRoomResponse> createRoom() {
        String roomId = RandomIdGenerator.randomId(RoomRule.ROOM_ID_LENGTH);
        Room room = new Room(roomId, Collections.emptyList());
        roomsService.createRoom(room);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateRoomResponse(roomId));
    }

    @GetMapping("/{roomId}/validate")
    public ResponseEntity<ValidateRoomResponse> validateRoom(@PathVariable String roomId) {
        boolean isValid = roomsService.exists(roomId) &&
                roomsService.getParticipants(roomId).size() < RoomRule.MAX_ROOM_PARTICIPANTS;
        return ResponseEntity.ok(new ValidateRoomResponse(isValid));
    }

    @PostMapping("/{roomId}/leave")
    public ResponseEntity<Void> forceLeave(@PathVariable String roomId, @RequestParam String userId) {
        leaveService.forceLeave(userId, roomId);
        return ResponseEntity.noContent().build();
    }
}
