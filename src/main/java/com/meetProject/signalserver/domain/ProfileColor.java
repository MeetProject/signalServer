package com.meetProject.signalserver.domain;

import com.meetProject.signalserver.exception.InvalidInputException;
import java.util.regex.Pattern;

public record ProfileColor(String value) {
    private static final Pattern HEX_COLOR_PATTERN =
            Pattern.compile("^#([0-9a-fA-F]{3}|[0-9a-fA-F]{4}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8})$");

    public ProfileColor {
        if (value == null || !HEX_COLOR_PATTERN.matcher(value).matches()) {
            throw new InvalidInputException("유효하지 않은 프로필 색상입니다.");
        }
    }
}
