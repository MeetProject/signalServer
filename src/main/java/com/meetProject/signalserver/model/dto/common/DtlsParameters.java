package com.meetProject.signalserver.model.dto.common;

import java.util.List;

public class DtlsParameters {
    private String role;
    private List<DtlsFingerprint> fingerprints;

    public static class DtlsFingerprint {
        private String algorithm;
        private String value;
    }
}
