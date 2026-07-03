package com.meetProject.signalserver.dto.socket;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.meetProject.signalserver.domain.MediaOption;
import com.meetProject.signalserver.domain.Participant;
import com.meetProject.signalserver.domain.User;
import java.util.List;

public record ParticipantDto(
        UserData user,
        @JsonProperty("isHandUp") boolean isHandUp,
        MediaOption mediaOption,
        List<String> producerIds
) {
    public static ParticipantDto from(Participant participant) {
        return new ParticipantDto(
                UserData.from(participant.getUser()),
                participant.isHandsUp(),
                participant.getMediaOption(),
                participant.getProducerIds()
        );
    }

    public record UserData(String userId, String userName, String profileColor) {
        public static UserData from(User user) {
            return new UserData(user.getId(), user.getName(), user.getProfileColor());
        }
    }
}
