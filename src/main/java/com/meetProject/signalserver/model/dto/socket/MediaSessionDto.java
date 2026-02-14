package com.meetProject.signalserver.model.dto.socket;

import com.meetProject.signalserver.model.dto.common.MediaPayload;
import com.meetProject.signalserver.model.dto.common.RtpCapabilitiesDto;
import com.meetProject.signalserver.model.dto.common.SignalResponse;

public class MediaSessionDto {
    public record CapabilitiesRequestPayload(String correlationId, String userId) implements MediaPayload {}
    public record CapabilitiesResponse(String correlationId, String userId, RtpCapabilitiesDto capabilities) implements
            SignalResponse {}
}
