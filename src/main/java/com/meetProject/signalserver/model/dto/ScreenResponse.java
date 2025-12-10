package com.meetProject.signalserver.model.dto;

import com.meetProject.signalserver.constant.SignalType;
import java.util.List;

public record ScreenResponse(String screenId, List<String> participants ) implements SignalResponse {
    private final static SignalType TYPE = SignalType.SCREEN;

    @Override
    public SignalType getSignalType() {
        return TYPE;
    }
}
