package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;
import com.meetProject.signalserver.model.User;
import java.util.List;

public record JoinResponse(String userId, String roomId, List<User> participants) implements SignalResponse {
    private final static SignalType TYPE = SignalType.JOIN;

    @Override
    public SignalType getSignalType() {
        return TYPE;
    }
}
