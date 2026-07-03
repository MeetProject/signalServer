package com.meetProject.signalserver.dto.application;

import com.meetProject.signalserver.domain.Participant;
import java.util.List;

public record ResyncResult(List<Participant> participants, boolean rejoinRequired) {
}
