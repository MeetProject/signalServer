package com.meetProject.signalserver.domain;

public record MediaOption(Boolean audio, Boolean video) {
    public MediaOption toggleAudio() {
        return new MediaOption(!audio, video);
    }

    public MediaOption toggleVideo() {
        return new MediaOption(audio, !video);
    }
}
