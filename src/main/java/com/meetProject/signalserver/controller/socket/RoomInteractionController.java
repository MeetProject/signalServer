package com.meetProject.signalserver.controller.socket;

import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.model.dto.socket.RoomInteractionDto.ChatPayload;
import com.meetProject.signalserver.model.dto.socket.RoomInteractionDto.DevicePayload;
import com.meetProject.signalserver.model.dto.socket.RoomInteractionDto.EmojiPayload;
import com.meetProject.signalserver.model.dto.socket.RoomInteractionDto.HandUpPayload;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.UserService;
import com.meetProject.signalserver.service.message.TopicMessagingService;
import com.meetProject.signalserver.util.WebSocketUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class RoomInteractionController {
    private final RoomsService roomsService;
    private final UserService userService;
    private final TopicMessagingService topicMessagingService;

    public RoomInteractionController(UserService userService, RoomsService roomsService, TopicMessagingService topicMessagingService) {
        this.roomsService = roomsService;
        this.userService = userService;
        this.topicMessagingService = topicMessagingService;
    }

    @MessageMapping("/chat/send")
    public void sendChat(@Payload ChatPayload chatPayload, SimpMessageHeaderAccessor header) {
        RoomValidate(chatPayload.roomId());
        String userId = WebSocketUtils.getUserId(header.getUser());
        topicMessagingService.sendChat(chatPayload.roomId(), userId, chatPayload.message());
    }

    @MessageMapping("/emoji")
    public void sendEmoji(@Payload EmojiPayload emojiPayload, SimpMessageHeaderAccessor header) {
        RoomValidate(emojiPayload.roomId());
        String userId = WebSocketUtils.getUserId(header.getUser());
        topicMessagingService.sendEmoji(emojiPayload.roomId(), userId, emojiPayload.emoji());
    }

    @MessageMapping("/device")
    public void sendDevice(@Payload DevicePayload devicePayload, SimpMessageHeaderAccessor header) {
        RoomValidate(devicePayload.roomId());
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = userService.getRoomId(userId);

        roomsService.handleMediaOption(roomId, userId, devicePayload.mediaOption());
        topicMessagingService.sendDevice(devicePayload.roomId(), userId, devicePayload.mediaOption());
    }

    @MessageMapping("/handUp")
    public void sendHandUp(@Payload HandUpPayload handUpPayload, SimpMessageHeaderAccessor header) {
        RoomValidate(handUpPayload.roomId());
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = userService.getRoomId(userId);

        roomsService.handleHandUp(roomId, userId, handUpPayload.value());
        topicMessagingService.sendHandUp(handUpPayload.roomId(), userId, handUpPayload.value());
    }

    private void RoomValidate(String roomId) {
        if(!roomsService.exists(roomId)) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_NOT_FOUND);
        }
    }
}
