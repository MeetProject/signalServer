package com.meetProject.signalserver.dto.socket;

import com.meetProject.signalserver.constant.DtlsDirection;
import com.meetProject.signalserver.dto.common.AppData;
import com.meetProject.signalserver.dto.common.ConsumerParams;
import com.meetProject.signalserver.dto.common.MediaPayload;
import com.meetProject.signalserver.dto.common.RtpCapabilities;
import com.meetProject.signalserver.dto.common.RtpParameters;
import com.meetProject.signalserver.dto.common.TransportOptions;
import com.meetProject.signalserver.dto.common.TransportOptions.DtlsParameters;

public class MediaSessionDto {
    public record CapabilitiesRequest(String correlationId, String userId, String roomId) implements MediaPayload {}
    public record CapabilitiesResponse(String correlationId, String userId, RtpCapabilities capabilities) {}

    public record DtlsRequest(String correlationId, String userId, String roomId, DtlsDirection direction) implements MediaPayload {}
    public record DtlsResponse(String correlationId, String userId, TransportOptions options) {}

    public record DtlsConnectRequest(String correlationId, String userId, String roomId, DtlsParameters dtlsParameters, String direction) implements MediaPayload {}
    public record DtlsConnectResponse(String correlationId, String userId) {}

    public record RtlsRequest(String correlationId, String userId, String roomId, AppData appData, String kind, RtpParameters rtpParameters) implements MediaPayload {}
    public record RtlsResponse(String correlationId, String userId, String producerId) {}

    public record ConsumerParamsRequest(String correlationId, String userId, String roomId, String targetId, String producerId, RtpCapabilities rtpCapabilities) implements MediaPayload {}
    public record ConsumerParamsResponse(String correlationId, String userId, ConsumerParams consumerParams) {}

    public record ConsumerResumeRequest(String consumerId) implements MediaPayload {
        @Override
        public String correlationId() {
            return null;
        }

        @Override
        public String userId() {
            return null;
        }
    }

    public record ConsumerPauseRequest(String consumerId) implements MediaPayload {
        @Override
        public String correlationId() {
            return null;
        }

        @Override
        public String userId() {
            return null;
        }
    }

    public record ProducerMuteRequest(String correlationId, String userId, String producerId) implements MediaPayload {}
    public record ProducerMuteResponse(String correlationId, String userId) {}

    public record MediaLeaveRequest(String roomId, String userId) implements MediaPayload {
        @Override
        public String correlationId() {
            return null;
        }
    }

    public record ProducerRemoveRequest(String userId, String producerId) implements MediaPayload {
        @Override
        public String correlationId() {
            return null;
        }
    }
}
