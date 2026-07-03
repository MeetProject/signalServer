package com.meetProject.signalserver.dto.application;

import com.meetProject.signalserver.domain.Participant;
import java.util.List;

public record JoinResult(Participant joiner, List<Participant> others) {
}
