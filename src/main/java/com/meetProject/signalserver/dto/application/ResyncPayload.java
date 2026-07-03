package com.meetProject.signalserver.dto.application;

import com.meetProject.signalserver.dto.socket.ParticipantDto;
import java.util.List;

public record ResyncPayload(List<ParticipantDto> participants, boolean rejoinRequired) {
}
