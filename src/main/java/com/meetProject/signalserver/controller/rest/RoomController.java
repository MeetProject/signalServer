package com.meetProject.signalserver.controller.rest;

import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.dto.rest.RoomSessionDto.CreateRoomResponse;
import com.meetProject.signalserver.dto.rest.RoomSessionDto.LeaveRequest;
import com.meetProject.signalserver.dto.rest.RoomSessionDto.ValidateRoomResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.LeaveResponse;
import com.meetProject.signalserver.infrastructure.StompMessageSender;
import com.meetProject.signalserver.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
    private final RoomService roomsService;
    private final StompMessageSender stompMessageSender;

    public RoomController(RoomService roomsService, StompMessageSender stompMessageSender) {
        this.roomsService = roomsService;
        this.stompMessageSender = stompMessageSender;
    }

    @PostMapping
    public ResponseEntity<CreateRoomResponse> createRoom() {
        CreateRoomResponse response = roomsService.save();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{roomId}/validate")
    public ResponseEntity<ValidateRoomResponse> validateRoom(@PathVariable String roomId) {
        ValidateRoomResponse response = roomsService.validate(roomId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/leave")
    public ResponseEntity<Void> leaveRoom(@RequestBody LeaveRequest leaveRequest) {
        roomsService.leave(leaveRequest.userId()).ifPresent(roomId ->
                stompMessageSender.broadcast(roomId, TopicType.LEAVE, new LeaveResponse(leaveRequest.userId())));
        return ResponseEntity.ok().build();
    }
}
