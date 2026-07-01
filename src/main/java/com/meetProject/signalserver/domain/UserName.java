package com.meetProject.signalserver.domain;

import com.meetProject.signalserver.exception.InvalidInputException;

public record UserName(String name) {
    public UserName(String name) {
        this.name = validateName(name);
    }

    private static String validateName(String name) {
        if(name.length() > 20) {
            throw new InvalidInputException("유효하지 않은 사용자 이름입니다.");
        }

        return name;
    }
}
