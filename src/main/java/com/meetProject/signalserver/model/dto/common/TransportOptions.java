package com.meetProject.signalserver.model.dto.common;

import java.util.List;

public class TransportOptions {
    private String id;
    private String direction; // "send" | "recv"
    private IceParameters iceParameters;
    private List<IceCandidate> iceCandidates;
    private DtlsParameters dtlsParameters;

    public static class IceParameters {
        private String usernameFragment;
        private String password;
        private Boolean iceLite;
    }

    public static class IceCandidate {
        private String foundation;
        private Integer priority;
        private String ip;
        private String protocol;
        private Integer port;
        private String type;
        private String tcpType;
    }

    public static class DtlsParameters {
        private String role;
        private List<DtlsFingerprint> fingerprints;
    }

    public static class DtlsFingerprint {
        private String algorithm;
        private String value;
    }
}
