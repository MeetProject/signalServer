package com.meetProject.signalserver.controller;

import com.meetProject.signalserver.model.dto.ChatPayload;
import com.meetProject.signalserver.model.dto.EmojiPayload;
import com.meetProject.signalserver.service.RoomsManagementService;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.service.UserManagementService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class RoomReactController {
    private final UserManagementService userManagementService;
    private final RoomsManagementService roomsManagementService;
    private final SignalMessagingService signalMessagingService;

    public RoomReactController(UserManagementService userManagementService, RoomsManagementService roomsManagementService, SignalMessagingService signalMessagingService) {
        this.userManagementService = userManagementService;
        this.roomsManagementService = roomsManagementService;
        this.signalMessagingService = signalMessagingService;
    }

    @MessageMapping("/chat/send")
    public void sendChat(@Payload ChatPayload chatPayload) {
        if (userManagementService.isUserNotExist(chatPayload.userId())) {
            throw new IllegalArgumentException("User does not exist");
        }

        if(roomsManagementService.isRoomNotExist(chatPayload.roomId())) {
            throw new IllegalArgumentException("Room does not exist");
        }

        signalMessagingService.sendChat(chatPayload.roomId(), chatPayload.userId(), chatPayload.message());
    }

    @MessageMapping("/emoji")
    public void sendEmoji(@Payload EmojiPayload emojiPayload) {
        if (userManagementService.isUserNotExist(emojiPayload.userId())) {
            throw new IllegalArgumentException("User does not exist");
        }

        if(roomsManagementService.isRoomNotExist(emojiPayload.roomId())) {
            throw new IllegalArgumentException("Room does not exist");
        }

        signalMessagingService.sendEmoji(emojiPayload.roomId(), emojiPayload.userId(), emojiPayload.emoji());
    }
}
