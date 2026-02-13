package com.meetProject.signalserver.model.dto.socket;

import com.meetProject.signalserver.model.dto.common.MediaOption;
import com.meetProject.signalserver.model.dto.common.User;
import com.meetProject.signalserver.model.dto.common.SignalResponse;
import java.util.List;

public class RoomSessionDto {
    public record JoinPayload(String roomId, String correlationId, MediaOption mediaOption) {}
    public record JoinResponse(String correlationId, List<User> participants) implements SignalResponse {}
}
