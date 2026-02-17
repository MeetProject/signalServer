package com.meetProject.signalserver.model.dto.socket;

import com.meetProject.signalserver.constant.Emoji;
import com.meetProject.signalserver.model.dto.common.MediaOption;
import com.meetProject.signalserver.model.dto.common.Participant;
import com.meetProject.signalserver.model.dto.common.TopicResponse;
import com.meetProject.signalserver.util.RandomIdGenerator;

public class RoomInteractionDto {
    public record ParticipantResponse(Participant participant) implements TopicResponse {
        @Override
        public String id() {
            return null;
        }
    }

    public record ProducerResponse(String producerId) implements TopicResponse {
        @Override
        public String id() {
            return null;
        }
    }

    public record ChatPayload(String roomId, String message) {}
    public record ChatResponse(String id, String userId, String message, long timestamp) implements TopicResponse {
        public ChatResponse(String userId, String message, long timestamp) {
            this(RandomIdGenerator.uuidGenerator(), userId, message, timestamp);
        }
    }

    public record DevicePayload ( String roomId, MediaOption mediaOption) {}
    public record DeviceResponse(String userId, MediaOption mediaOption) implements TopicResponse {
        @Override
        public String id() {
            return null;
        }
    }

    public record EmojiPayload(String roomId, Emoji emoji) {}
    public record EmojiResponse(String id, String userId, Emoji emoji, long timestamp) implements TopicResponse {
        public EmojiResponse(String userId, Emoji emoji, long timestamp) {
            this(RandomIdGenerator.uuidGenerator(), userId, emoji, timestamp);
        }
    }

    public record HandUpPayload (String roomId, boolean value) {}
    public record HandUpResponse (String userId, boolean value) implements TopicResponse {
        @Override
        public String id() {
            return null;
        }
    }
}
