package com.meetProject.signalserver.controller.socket;

import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.model.dto.socket.RoomInteractionDto.ChatPayload;
import com.meetProject.signalserver.model.dto.socket.RoomInteractionDto.DevicePayload;
import com.meetProject.signalserver.model.dto.socket.RoomInteractionDto.EmojiPayload;
import com.meetProject.signalserver.model.dto.socket.RoomInteractionDto.HandUpPayload;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.message.TopicMessagingService;
import com.meetProject.signalserver.util.WebSocketUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class RoomInteractionController {
    private final RoomsService roomsService;
    private final TopicMessagingService topicMessagingService;

    public RoomInteractionController(RoomsService roomsService, TopicMessagingService topicMessagingService) {
        this.roomsService = roomsService;
        this.topicMessagingService = topicMessagingService;
    }

    @MessageMapping("/chat/send")
    public void sendChat(@Payload ChatPayload chatPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = roomsService.getRoomId(userId);

        if(roomId == null) {
            throw new IllegalArgumentException(ErrorMessage.USER_NOT_JOINED);
        }

        topicMessagingService.sendChat(roomId, userId, chatPayload.message());
    }

    @MessageMapping("/emoji")
    public void sendEmoji(@Payload EmojiPayload emojiPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());

        String roomId = roomsService.getRoomId(userId);

        if(roomId == null) {
            throw new IllegalArgumentException(ErrorMessage.USER_NOT_JOINED);
        }

        topicMessagingService.sendEmoji(roomId, userId, emojiPayload.emoji());
    }

    @MessageMapping("/device")
    public void sendDevice(@Payload DevicePayload devicePayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = roomsService.getRoomId(userId);

        if(roomId == null) {
            throw new IllegalArgumentException(ErrorMessage.USER_NOT_JOINED);
        }

        roomsService.handleMediaOption(roomId, userId, devicePayload.mediaOption());
        topicMessagingService.sendDevice(roomId, userId, devicePayload.mediaOption());
    }

    @MessageMapping("/handUp")
    public void sendHandUp(@Payload HandUpPayload handUpPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = roomsService.getRoomId(userId);

        if(roomId == null) {
            throw new IllegalArgumentException(ErrorMessage.USER_NOT_JOINED);
        }

        roomsService.handleHandUp(roomId, userId, handUpPayload.value());
        topicMessagingService.sendHandUp(roomId, userId, handUpPayload.value());
    }

    @MessageMapping("/leave")
    public void sendLeave(SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = roomsService.getRoomId(userId);

        if(roomId == null) {
            throw new IllegalArgumentException(ErrorMessage.USER_NOT_JOINED);
        }

        roomsService.removeParticipant(roomId, userId);
        topicMessagingService.sendLeave(userId, roomId);
    }
}
