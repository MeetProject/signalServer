package com.meetProject.signalserver.controller;

import com.meetProject.signalserver.model.dto.ChatPayload;
import com.meetProject.signalserver.model.dto.EmojiPayload;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
public class RoomReactController {
    private final UserService userService;
    private final RoomsService roomsService;
    private final SignalMessagingService signalMessagingService;

    public RoomReactController(UserService userService, RoomsService roomsService, SignalMessagingService signalMessagingService) {
        this.userService = userService;
        this.roomsService = roomsService;
        this.signalMessagingService = signalMessagingService;
    }

    @MessageMapping("/chat/send")
    public void sendChat(@Payload ChatPayload chatPayload) {
        if (!userService.exists(chatPayload.userId())) {
            throw new IllegalArgumentException("User does not exist");
        }

        if(!roomsService.exists(chatPayload.roomId())) {
            throw new IllegalArgumentException("Room does not exist");
        }

        signalMessagingService.sendChat(chatPayload.roomId(), chatPayload.userId(), chatPayload.message());
    }

    @MessageMapping("/emoji")
    public void sendEmoji(@Payload EmojiPayload emojiPayload) {
        if (!userService.exists(emojiPayload.userId())) {
            throw new IllegalArgumentException("User does not exist");
        }

        if(!roomsService.exists(emojiPayload.roomId())) {
            throw new IllegalArgumentException("Room does not exist");
        }

        signalMessagingService.sendEmoji(emojiPayload.roomId(), emojiPayload.userId(), emojiPayload.emoji());
    }
}
