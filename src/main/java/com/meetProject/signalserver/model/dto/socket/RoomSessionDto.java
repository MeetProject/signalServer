package com.meetProject.signalserver.model.dto.socket;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.model.dto.common.MediaOption;
import com.meetProject.signalserver.model.dto.common.User;
import com.meetProject.signalserver.model.dto.common.SignalResponse;
import java.util.List;

public class RoomSessionDto {
    public record JoinPayload(String roomId, MediaOption mediaOption) {}
    public record JoinResponse(String userId, String roomId, List<User> participants) implements SignalResponse {
        private final static SignalType TYPE = SignalType.JOIN;

        @Override
        public SignalType getSignalType() {
            return TYPE;
        }
    }

}
