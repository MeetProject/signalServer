package com.meetProject.signalserver.controller.rest;

import com.meetProject.signalserver.constant.MediaType;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.dto.rest.RoomDto.CreateRoomResponse;
import com.meetProject.signalserver.dto.rest.RoomDto.LeaveRequest;
import com.meetProject.signalserver.dto.rest.RoomDto.ValidateRoomResponse;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.MediaLeaveRequest;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.LeaveResponse;
import com.meetProject.signalserver.infrastructure.StompMessageSender;
import com.meetProject.signalserver.service.RoomService;
import jakarta.validation.Valid;
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
    private final RoomService roomService;
    private final StompMessageSender stompMessageSender;

    public RoomController(RoomService roomService, StompMessageSender stompMessageSender) {
        this.roomService = roomService;
        this.stompMessageSender = stompMessageSender;
    }

    @PostMapping
    public ResponseEntity<CreateRoomResponse> createRoom() {
        String roomId = roomService.save();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateRoomResponse(roomId));
    }

    @GetMapping("/{roomId}/validate")
    public ResponseEntity<ValidateRoomResponse> validateRoom(@PathVariable String roomId) {
        return ResponseEntity.ok(new ValidateRoomResponse(roomService.validate(roomId)));
    }

    @PostMapping("/leave")
    public ResponseEntity<Void> leaveRoom(@Valid @RequestBody LeaveRequest leaveRequest) {
        String userId = leaveRequest.userId();
        roomService.leave(userId).ifPresent(roomId -> {
            stompMessageSender.broadcast(roomId, TopicType.LEAVE, new LeaveResponse(userId));
            stompMessageSender.sendToMediaServer(MediaType.LEAVE, new MediaLeaveRequest(roomId, userId));
        });
        return ResponseEntity.ok().build();
    }
}
