package com.meetProject.signalserver.dto.socket;

import com.meetProject.signalserver.constant.Emoji;
import com.meetProject.signalserver.domain.MediaOption;
import com.meetProject.signalserver.util.RandomIdGenerator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RoomInteractionDto {
    public record ParticipantResponse(ParticipantDto participant) implements TopicResponse {}

    public record ProducerResponse(String userId, String producerId) implements TopicResponse {}

    public record UserProducerRemoveRequest(
            @NotBlank(message = "producerId는 필수입니다.") String producerId,
            @NotBlank(message = "trackType은 필수입니다.") String trackType
    ) {}
    public record ProducerRemoveResponse(String userId, String trackType) implements TopicResponse {}

    public record ChatRequest(@NotBlank(message = "메시지를 입력해주세요.") String message) {}
    public record ChatResponse(String id, String userId, String message, long timestamp) implements TopicResponse {
        public static ChatResponse of(String userId, String message) {
            return new ChatResponse(RandomIdGenerator.uuidGenerator(), userId, message, System.currentTimeMillis());
        }
    }

    public record DeviceRequest(@NotNull(message = "미디어 옵션은 필수입니다.") MediaOption mediaOption) {}
    public record DeviceResponse(String userId, MediaOption mediaOption) implements TopicResponse {}

    public record EmojiRequest(@NotNull(message = "이모지를 선택해주세요.") Emoji emoji) {}
    public record EmojiResponse(String id, String userId, Emoji emoji, long timestamp) implements TopicResponse {
        public static EmojiResponse of(String userId, Emoji emoji) {
            return new EmojiResponse(RandomIdGenerator.uuidGenerator(), userId, emoji, System.currentTimeMillis());
        }
    }

    public record HandsUpResponse(String userId, boolean value) implements TopicResponse {}

    public record LeaveResponse(String userId) implements TopicResponse {}
}
