package com.meetProject.signalserver.model.dto.socket;

import com.meetProject.signalserver.constant.DtlsDirection;
import com.meetProject.signalserver.model.dto.common.MediaPayload;
import com.meetProject.signalserver.model.dto.common.RtpCapabilitiesDto;
import com.meetProject.signalserver.model.dto.common.TransportOptions;

public class MediaSessionDto {
    public record CapabilitiesRequestPayload(String correlationId, String userId) implements MediaPayload {}
    public record CapabilitiesResponse(String correlationId, String userId, RtpCapabilitiesDto capabilities) {}

    public record DtlsRequestPayload(String correlationId, String userId, DtlsDirection direction) implements MediaPayload {}
    public record DtlsResponse(String correlationId, String userId, TransportOptions options, DtlsDirection direction) {}
}
