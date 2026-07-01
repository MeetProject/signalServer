package com.meetProject.signalserver.dto.socket;

import com.meetProject.signalserver.constant.Emoji;
import com.meetProject.signalserver.domain.Participant;
import com.meetProject.signalserver.domain.MediaOption;
import com.meetProject.signalserver.dto.common.TopicResponse;
import com.meetProject.signalserver.util.RandomIdGenerator;

public class RoomInteractionDto {
    public record ParticipantResponse(Participant participant) implements TopicResponse {
        @Override
        public String id() {
            return null;
        }
    }

    public record ProducerResponse(String userId, String producerId) implements TopicResponse {
        @Override
        public String id() {
            return null;
        }
    }

    public record RemoveProducerRequest(String producerId, String trackType) {}
    public record RemoveProducerResponse(String userId, String trackType) implements TopicResponse {
        @Override
        public String id() {
            return null;
        }
    }

    public record ChatRequest(String message) {}
    public record ChatResponse(String id, String userId, String message, long timestamp) implements TopicResponse {
        public static ChatResponse of(String userId, String message) {
            return new ChatResponse(RandomIdGenerator.uuidGenerator(), userId, message, System.currentTimeMillis());
        }
    }

    public record DeviceRequest (MediaOption mediaOption) {}
    public record DeviceResponse(String userId, MediaOption mediaOption) implements TopicResponse {
        @Override
        public String id() {
            return null;
        }
    }

    public record EmojiRequest(Emoji emoji) {}
    public record EmojiResponse(String id, String userId, Emoji emoji, long timestamp) implements TopicResponse {
        public static EmojiResponse of(String userId, Emoji emoji) {
            return new EmojiResponse(RandomIdGenerator.uuidGenerator(), userId, emoji, System.currentTimeMillis());
        }
    }

    public record HandUpResponse (String userId, boolean value) implements TopicResponse {
        @Override
        public String id() {
            return null;
        }
    }

    public record LeaveResponse(String userId) implements TopicResponse {
        @Override
        public String id() {
            return null;
        }
    }
}
