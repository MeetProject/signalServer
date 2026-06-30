package com.meetProject.signalserver.dto.common;

import java.util.List;

public record TransportOptions(
        String id,
        String direction,
        IceParameters iceParameters,
        List<IceCandidate> iceCandidates,
        DtlsParameters dtlsParameters,
        Object sctpParameters,
        Object iceServers,
        Object iceTransportPolicy,
        Object additionalSettings,
        Object appData
) {
    public record IceParameters(
            String usernameFragment,
            String password,
            Boolean iceLite
    ) {}

    public record IceCandidate(
            String foundation,
            Integer priority,
            String ip,
            String protocol,
            Integer port,
            String type,
            String tcpType
    ) {}

    public record DtlsParameters(
            String role,
            List<DtlsFingerprint> fingerprints
    ) {}

    public record DtlsFingerprint(
            String algorithm,
            String value
    ) {}
}