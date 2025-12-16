package com.meetProject.signalserver.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.model.dto.ChatPayload;
import com.meetProject.signalserver.model.dto.DevicePayload;
import com.meetProject.signalserver.model.dto.EmojiPayload;
import com.meetProject.signalserver.model.dto.HandUpPayload;
import com.meetProject.signalserver.model.dto.ParticipantResponse;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.SignalMessagingService;
import org.springframework.stereotype.Component;

@Component
public class TopicHandler {
    private final SignalMessagingService signalMessagingService;
    private final RoomsService roomsService;
    private final ObjectMapper objectMapper;

    public TopicHandler(SignalMessagingService signalMessagingService, RoomsService roomsService, ObjectMapper objectMapper) {
        this.roomsService = roomsService;
        this.objectMapper = objectMapper;
        this.signalMessagingService = signalMessagingService;
    }


    public void handleMessage(String userId, String path, Object payload) {

        switch(path){
            case "CHAT" -> {
                chat(userId, payload);
            }
            case "EMOJI" -> {
                emoji(userId, payload);
            }
            case "DEVICE" -> {
                device(userId, payload);
            }
            case "HANDUP" -> {
                handUp(userId, payload);
            }
        }
    }

    private void roomValidate(String roomId) {
        if(!roomsService.exists(roomId)) {
            throw new IllegalArgumentException(ErrorMessage.ROOM_NOT_FOUND);
        }
    }

    private void chat(String userId, Object payload) {
        ChatPayload chatPayload = objectMapper.convertValue(payload, ChatPayload.class);
        roomValidate(chatPayload.roomId());
        signalMessagingService.sendChat(chatPayload.roomId(), userId, chatPayload.message());
    }

    private void emoji(String userId, Object payload) {
        EmojiPayload emojiPayload = objectMapper.convertValue(payload, EmojiPayload.class);
        roomValidate(emojiPayload.roomId());
        signalMessagingService.sendEmoji(emojiPayload.roomId(), userId, emojiPayload.emoji());
    }

    private void device(String userId, Object payload) {
        DevicePayload devicePayload = objectMapper.convertValue(payload, DevicePayload.class);
        roomValidate(devicePayload.roomId());
        signalMessagingService.sendDevice(devicePayload.roomId(), userId, devicePayload.mediaOption());
    }

    private void handUp(String userId, Object payload) {
        HandUpPayload handUpPayload = objectMapper.convertValue(payload, HandUpPayload.class);
        roomValidate(handUpPayload.roomId());
        signalMessagingService.sendHandUp(handUpPayload.roomId(), userId, handUpPayload.value());
    }

}
