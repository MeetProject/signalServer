package com.meetProject.signalserver.model.dto.socket;

import com.meetProject.signalserver.model.dto.common.MediaOption;
import com.meetProject.signalserver.model.dto.common.RtpCapabilitiesDto;
import com.meetProject.signalserver.model.dto.common.User;
import com.meetProject.signalserver.model.dto.common.SignalResponse;
import java.util.List;

public class RoomSessionDto {
    public record UserCapabilityPayload(String correlationId) {}
    public record UserCapabilityResponse(String correlationId, RtpCapabilitiesDto capabilities) implements SignalResponse {}
    public record JoinPayload(String roomId, String correlationId, String produceId, MediaOption mediaOption) {}
    public record JoinResponse(String correlationId, List<User> participants) implements SignalResponse {}
}
