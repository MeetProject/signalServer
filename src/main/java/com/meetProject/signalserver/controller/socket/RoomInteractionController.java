package com.meetProject.signalserver.controller.socket;

import com.meetProject.signalserver.constant.ErrorMessage;
import com.meetProject.signalserver.model.dto.socket.MediaSessionDto.*;
import com.meetProject.signalserver.model.dto.socket.RoomInteractionDto.*;
import com.meetProject.signalserver.model.dto.socket.RoomSessionDto.UserConsumerPausePayload;
import com.meetProject.signalserver.model.dto.socket.RoomSessionDto.UserConsumerResumePayload;
import com.meetProject.signalserver.service.RoomsService;
import com.meetProject.signalserver.service.message.MediaMessagingService;
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
    private final MediaMessagingService mediaMessagingService;

    public RoomInteractionController(RoomsService roomsService, TopicMessagingService topicMessagingService, MediaMessagingService mediaMessagingService) {
        this.roomsService = roomsService;
        this.topicMessagingService = topicMessagingService;
        this.mediaMessagingService = mediaMessagingService;
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

    @MessageMapping("/producer/remove")
    public void removeTrack(@Payload RemoveProducerPayload removeProducerPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = roomsService.getRoomId(userId);
        String producerId = removeProducerPayload.producerId();

        if(roomId == null) {
            throw new IllegalArgumentException(ErrorMessage.USER_NOT_JOINED);
        }

        roomsService.removeProducer(roomId, userId, producerId);

        topicMessagingService.sendRemoveProducerId(roomId, userId, removeProducerPayload.trackType());
        mediaMessagingService.sendProducerRemove(new ProducerRemovePayload(userId, producerId));
    }

    @MessageMapping("/consumer/resume")
    public void consumerResume(@Payload UserConsumerResumePayload resumePayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());

        ConsumerResumeRequestPayload payload = new ConsumerResumeRequestPayload(resumePayload.consumerId());
        mediaMessagingService.sendConsumerResume(payload);
    }

    @MessageMapping("/consumer/pause")
    public void consumerPause(@Payload UserConsumerPausePayload pausePayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());

        ConsumerPauseRequestPayload payload = new ConsumerPauseRequestPayload(pausePayload.consumerId());
        mediaMessagingService.sendConsumerPause(payload);
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
        mediaMessagingService.sendLeave(new MediaLeavePayload(userId, roomId));
    }
}
