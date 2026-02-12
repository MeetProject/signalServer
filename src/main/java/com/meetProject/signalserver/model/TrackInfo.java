package com.meetProject.signalserver.model;

import com.meetProject.signalserver.constant.StreamType;

public record TrackInfo(String userId, StreamType streamType) {}
