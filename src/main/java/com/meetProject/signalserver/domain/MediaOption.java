package com.meetProject.signalserver.domain;

import com.meetProject.signalserver.exception.InvalidInputException;

public record MediaOption(Boolean audio, Boolean video) {
    public MediaOption {
        if (audio == null || video == null) {
            throw new InvalidInputException("유효하지 않은 미디어 옵션입니다.");
        }
    }
}
