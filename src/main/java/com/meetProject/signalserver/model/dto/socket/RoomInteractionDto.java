package com.meetProject.signalserver.model.dto.socket;

import com.meetProject.signalserver.constant.Emoji;
import com.meetProject.signalserver.model.dto.common.MediaOption;
import com.meetProject.signalserver.model.dto.common.TopicResponse;
import com.meetProject.signalserver.model.dto.common.User;
import com.meetProject.signalserver.util.RandomIdGenerator;

public class RoomInteractionDto {
    public record ParticipantResponse(User user, MediaOption mediaOption) implements TopicResponse {
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
    public record ChatResponse(String userId, String message, long timestamp) implements TopicResponse {
        private final static String id = RandomIdGenerator.uuidGenerator();

        @Override
        public String id() {
            return id;
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
    public record EmojiResponse(String userId, Emoji emoji, long timestamp) implements TopicResponse {
        private static  final String id = RandomIdGenerator.uuidGenerator();

        @Override
        public String id() {
            return id;
        }
    }

    public record HandUpPayload (String roomId, boolean value) {}
    public record HandUpResponse (String userId, boolean value) implements TopicResponse {
        private static final String id = RandomIdGenerator.uuidGenerator();

        @Override
        public String id() {
            return null;
        }
    }
}
