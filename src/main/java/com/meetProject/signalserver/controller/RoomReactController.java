package com.meetProject.signalserver.controller;

import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.model.dto.ChatPayload;
import com.meetProject.signalserver.model.dto.DevicePayload;
import com.meetProject.signalserver.model.dto.EmojiPayload;
import com.meetProject.signalserver.model.dto.HandUpPayload;
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
    private final UserService userService;
    private final SignalMessagingService signalMessagingService;

    public RoomReactController(UserService userService, RoomsService roomsService, SignalMessagingService signalMessagingService) {
        this.roomsService = roomsService;
        this.userService = userService;
        this.signalMessagingService = signalMessagingService;
    }

    @MessageMapping("/chat/send")
    public void sendChat(@Payload ChatPayload chatPayload, SimpMessageHeaderAccessor header) {
        RoomValidate(chatPayload.roomId());
        String userId = WebSocketUtils.getUserId(header.getUser());
        signalMessagingService.sendChat(chatPayload.roomId(), userId, chatPayload.message());
    }

    @MessageMapping("/emoji")
    public void sendEmoji(@Payload EmojiPayload emojiPayload, SimpMessageHeaderAccessor header) {
        RoomValidate(emojiPayload.roomId());
        String userId = WebSocketUtils.getUserId(header.getUser());
        signalMessagingService.sendEmoji(emojiPayload.roomId(), userId, emojiPayload.emoji());
    }

    @MessageMapping("/device")
    public void sendDevice(@Payload DevicePayload devicePayload, SimpMessageHeaderAccessor header) {
        RoomValidate(devicePayload.roomId());
        String userId = WebSocketUtils.getUserId(header.getUser());
        signalMessagingService.sendDevice(devicePayload.roomId(), userId, devicePayload.mediaOption());
    }

    @MessageMapping("/handUp")
    public void sendHandUp(@Payload HandUpPayload handUpPayload, SimpMessageHeaderAccessor header) {
        RoomValidate(handUpPayload.roomId());
        String userId = WebSocketUtils.getUserId(header.getUser());
        userService.updateUserHandUpStatus(userId, handUpPayload.value());
        signalMessagingService.sendHandUp(handUpPayload.roomId(), userId, handUpPayload.value());
    }

    private void RoomValidate(String roomId) {
        if(!roomsService.exists(roomId)) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_NOT_FOUND);
        }
    }
}
