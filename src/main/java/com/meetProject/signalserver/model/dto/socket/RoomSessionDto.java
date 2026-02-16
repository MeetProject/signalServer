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
import com.meetProject.signalserver.model.dto.common.User;
import com.meetProject.signalserver.model.dto.common.SignalResponse;
import java.util.List;

public class RoomSessionDto {
    public record UserCapabilityPayload(String correlationId) {}
    public record UserCapabilityResponse(String correlationId, RtpCapabilities capabilities) implements SignalResponse {}

    public record UserDtlsPayload(String correlationId, DtlsDirection direction) {}
    public record UserDtlsResponse(String correlationId, TransportOptions options, DtlsDirection direction, List<String> producers) implements SignalResponse {}

    public record UserDtlsConnectPayload(String correlationId, String transportId, DtlsParameters dtlsParameters) {}
    public record UserDtlsConnectResponse(String correlationId) implements SignalResponse {}

    public record UserRtlsPayload(String correlationId, AppData appData, RtpParameters rtpParameters, String transportId) {}
    public record UserRtlsResponse(String correlationId, String producerId) implements SignalResponse {}

    public record UserConsumerParamsPayload(String correlationId, String producerId, RtpCapabilities rtpCapabilities, String transportId) {}
    public record UserConsumerParamsResponse(String correlationId, ConsumerParams consumerParams) implements SignalResponse {}

    public record UserResumePayload(String correlationId, String consumerId) {}
    public record UserResumeResponse(String correlationId) implements SignalResponse {}

    public record JoinPayload(String roomId, String correlationId, MediaOption mediaOption) {}
    public record JoinResponse(String correlationId, List<Participant> participants) implements SignalResponse {}
}
