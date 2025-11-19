package com.meetProject.signalserver.controller;

import com.meetProject.signalserver.model.dto.ChatPayload;
import com.meetProject.signalserver.model.dto.DevicePayload;
import com.meetProject.signalserver.model.dto.EmojiPayload;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.SignalMessagingService;
import com.meetProject.signalserver.service.UserService;
import com.meetProject.signalserver.util.WebSocketUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class RoomReactController {
    private final RoomsService roomsService;
    private final SignalMessagingService signalMessagingService;

    public RoomReactController(UserService userService, RoomsService roomsService, SignalMessagingService signalMessagingService) {
        this.roomsService = roomsService;
        this.signalMessagingService = signalMessagingService;
    }

    @MessageMapping("/chat/send")
    public void sendChat(@Payload ChatPayload chatPayload, SimpMessageHeaderAccessor header) {
        if(!roomsService.exists(chatPayload.roomId())) {
            throw new IllegalArgumentException("Room does not exist");
        }
        String userId = WebSocketUtils.getUserId(header.getUser());
        signalMessagingService.sendChat(chatPayload.roomId(), userId, chatPayload.message());
    }

    @MessageMapping("/emoji")
    public void sendEmoji(@Payload EmojiPayload emojiPayload, SimpMessageHeaderAccessor header) {
        if(!roomsService.exists(emojiPayload.roomId())) {
            throw new IllegalArgumentException("Room does not exist");
        }
        String userId = WebSocketUtils.getUserId(header.getUser());
        signalMessagingService.sendEmoji(emojiPayload.roomId(), userId, emojiPayload.emoji());
    }

    @MessageMapping("/device")
    public void sendDevice(@Payload DevicePayload devicePayload, SimpMessageHeaderAccessor header) {
        if(!roomsService.exists(devicePayload.roomId())) {
            throw new IllegalArgumentException("Room does not exist");
        }
        String userId = WebSocketUtils.getUserId(header.getUser());
        signalMessagingService.sendDevice(devicePayload.roomId(), userId, devicePayload.mediaOption());
    }
}
