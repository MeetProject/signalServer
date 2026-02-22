package com.meetProject.signalserver.model.dto.socket;

import com.meetProject.signalserver.constant.DtlsDirection;
import com.meetProject.signalserver.model.dto.common.AppData;
import com.meetProject.signalserver.model.dto.common.ConsumerParams;
import com.meetProject.signalserver.model.dto.common.MediaPayload;
import com.meetProject.signalserver.model.dto.common.RtpCapabilities;
import com.meetProject.signalserver.model.dto.common.RtpParameters;
import com.meetProject.signalserver.model.dto.common.TransportOptions;
import com.meetProject.signalserver.model.dto.common.TransportOptions.DtlsParameters;

public class MediaSessionDto {
    public record CapabilitiesRequestPayload(String correlationId, String userId, String roomId) implements MediaPayload {}
    public record CapabilitiesResponse(String correlationId, String userId, RtpCapabilities capabilities) {}

    public record DtlsRequestPayload(String correlationId, String userId, String roomId, DtlsDirection direction) implements MediaPayload {}
    public record DtlsResponse(String correlationId, String userId, TransportOptions options) {}

    public record DtlsConnectPayload(String correlationId, String userId, String roomId, DtlsParameters dtlsParameters, String direction) implements MediaPayload {}
    public record DtlsConnectResponse(String correlationId, String userId) {}

    public record RtlsRequestPayload(String correlationId, String userId, AppData appData, String kind, RtpParameters rtpParameters) implements MediaPayload {}
    public record RtlsResponse(String correlationId, String userId, String producerId) {}

    public record ConsumerParamsRequestPayload(String correlationId, String userId, String roomId, String targetId, String producerId, RtpCapabilities rtpCapabilities) implements MediaPayload {}
    public record ConsumerParamsResponse(String correlationId, String userId, ConsumerParams consumerParams) {}

    public record ResumeRequestPayload(String correlationId, String userId, String consumerId) implements MediaPayload {}
    public record ResumeResponse(String correlationId, String userId) {}
}
