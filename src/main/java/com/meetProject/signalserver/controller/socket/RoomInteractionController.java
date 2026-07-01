package com.meetProject.signalserver.controller.socket;

import com.meetProject.signalserver.constant.MediaType;
import com.meetProject.signalserver.constant.TopicType;
import com.meetProject.signalserver.dto.application.HandsUpPayload;
import com.meetProject.signalserver.dto.application.ProducerPayload;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ConsumerPauseRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ConsumerResumeRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.MediaLeaveRequest;
import com.meetProject.signalserver.dto.socket.MediaSessionDto.ProducerRemoveRequest;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.ChatRequest;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.ChatResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.DeviceRequest;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.DeviceResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.EmojiRequest;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.EmojiResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.HandUpResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.LeaveResponse;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.RemoveProducerRequest;
import com.meetProject.signalserver.dto.socket.RoomInteractionDto.RemoveProducerResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserConsumerPauseRequest;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.UserConsumerResumeRequest;
import com.meetProject.signalserver.infrastructure.StompMessageSender;
import com.meetProject.signalserver.service.ParticipantService;
import com.meetProject.signalserver.service.RoomService;
import com.meetProject.signalserver.util.WebSocketUtils;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class RoomInteractionController {
    private final ParticipantService participantService;
    private final RoomService roomService;
    private final StompMessageSender stompMessageSender;

    public RoomInteractionController(ParticipantService participantService, RoomService roomService, StompMessageSender stompMessageSender) {
        this.participantService = participantService;
        this.roomService = roomService;
        this.stompMessageSender = stompMessageSender;
    }

    @MessageMapping("/chat/send")
    public void sendChat(@Payload ChatRequest chatPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = participantService.getJoinedRoomId(userId);

        stompMessageSender.broadcast(roomId, TopicType.CHAT, ChatResponse.of(userId, chatPayload.message()));
    }

    @MessageMapping("/emoji")
    public void sendEmoji(@Payload EmojiRequest emojiPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = participantService.getJoinedRoomId(userId);

        stompMessageSender.broadcast(roomId, TopicType.EMOJI, EmojiResponse.of(userId, emojiPayload.emoji()));
    }

    @MessageMapping("/device")
    public void sendDevice(@Payload DeviceRequest devicePayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        String roomId = participantService.getJoinedRoomId(userId);

        stompMessageSender.broadcast(roomId, TopicType.DEVICE, new DeviceResponse(userId, devicePayload.mediaOption()));
    }

    @MessageMapping("/producer/remove")
    public void removeTrack(@Payload RemoveProducerRequest removeProducerPayload, SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        ProducerPayload deletion = participantService.removeProducer(userId, removeProducerPayload.producerId());

        stompMessageSender.sendToMediaServer(MediaType.PRODUCER_REMOVE, new ProducerRemoveRequest(userId, deletion.producerId()));
        stompMessageSender.broadcast(deletion.roomId(), TopicType.PRODUCER_REMOVE, new RemoveProducerResponse(userId, removeProducerPayload.trackType()));
    }

    @MessageMapping("/consumer/resume")
    public void consumerResume(@Payload UserConsumerResumeRequest resumePayload) {
        stompMessageSender.sendToMediaServer(MediaType.CONSUMER_RESUME, new ConsumerResumeRequest(resumePayload.consumerId()));
    }

    @MessageMapping("/consumer/pause")
    public void consumerPause(@Payload UserConsumerPauseRequest pausePayload) {
        stompMessageSender.sendToMediaServer(MediaType.CONSUMER_PAUSE, new ConsumerPauseRequest(pausePayload.consumerId()));
    }

    @MessageMapping("/handUp")
    public void sendHandUp(SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        HandsUpPayload payload = participantService.toggleHandsUp(userId);

        stompMessageSender.broadcast(payload.roomId(), TopicType.HANDUP, new HandUpResponse(payload.userId(), payload.isHandsUp()));
    }

    @MessageMapping("/leave")
    public void sendLeave(SimpMessageHeaderAccessor header) {
        String userId = WebSocketUtils.getUserId(header.getUser());
        roomService.leave(userId).ifPresent(roomId -> {
            stompMessageSender.broadcast(roomId, TopicType.LEAVE, new LeaveResponse(userId));
            stompMessageSender.sendToMediaServer(MediaType.LEAVE, new MediaLeaveRequest(roomId, userId));
        });
    }
}
