package com.meetProject.signalserver.model.dto.socket;

import com.meetProject.signalserver.constant.DtlsDirection;
import com.meetProject.signalserver.model.dto.common.AppData;
import com.meetProject.signalserver.model.dto.common.ConsumerParams;
import com.meetProject.signalserver.model.dto.common.MediaOption;
import com.meetProject.signalserver.model.dto.common.Participant;
import com.meetProject.signalserver.model.dto.common.RtpCapabilities;
import com.meetProject.signalserver.model.dto.common.RtpParameters;
import com.meetProject.signalserver.model.dto.common.TransportOptions;
import com.meetProject.signalserver.model.dto.common.TransportOptions.DtlsParameters;
import com.meetProject.signalserver.model.dto.common.SignalResponse;
import java.util.List;

public class RoomSessionDto {
    public record UserCapabilityPayload(String correlationId) {}
    public record UserCapabilityResponse(String correlationId, RtpCapabilities capabilities) implements SignalResponse {}

    public record UserDtlsPayload(String correlationId, DtlsDirection direction) {}
    public record UserDtlsResponse(String correlationId, TransportOptions options) implements SignalResponse {}

    public record UserDtlsConnectPayload(String correlationId, DtlsParameters dtlsParameters, String direction) {}
    public record UserDtlsConnectResponse(String correlationId) implements SignalResponse {}

    public record UserRtlsPayload(String correlationId, AppData appData, String kind, RtpParameters rtpParameters) {}
    public record UserRtlsResponse(String correlationId, String producerId) implements SignalResponse {}

    public record UserConsumerParamsPayload(String correlationId, String targetId, String producerId, RtpCapabilities rtpCapabilities) {}
    public record UserConsumerParamsResponse(String correlationId, ConsumerParams consumerParams) implements SignalResponse {}

    public record UserResumePayload(String correlationId, String consumerId) {}
    public record UserResumeResponse(String correlationId) implements SignalResponse {}

    public record JoinPayload(String roomId, String correlationId, MediaOption mediaOption) {}
    public record JoinResponse(String correlationId, List<Participant> participants) implements SignalResponse {}
}
