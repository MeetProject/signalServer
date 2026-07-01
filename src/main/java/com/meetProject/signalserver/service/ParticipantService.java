package com.meetProject.signalserver.service;

import com.meetProject.signalserver.dto.application.HandsUpPayload;
import com.meetProject.signalserver.dto.application.ProducerPayload;
import com.meetProject.signalserver.exception.ParticipantNotJoinedException;
import com.meetProject.signalserver.repository.ParticipantRepository;
import org.springframework.stereotype.Service;

@Service
public class ParticipantService {
    private final ParticipantRepository participantRepository;

    public ParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public ProducerPayload registerProducer(String userId, String producerId) {
        String roomId = getJoinedRoomId(userId);
        participantRepository.addProducer(roomId, userId, producerId);
        return new ProducerPayload(roomId, userId, producerId);
    }

    public ProducerPayload removeProducer(String userId, String producerId) {
        String roomId = getJoinedRoomId(userId);
        participantRepository.removeProducerId(roomId, userId, producerId);
        return new ProducerPayload(roomId, userId, producerId);
    }

    public String getJoinedRoomId(String userId) {
        return participantRepository.findRoomIdByUserId(userId)
                .orElseThrow(ParticipantNotJoinedException::new);
    }

    public HandsUpPayload toggleHandsUp(String userId) {
        String roomId = getJoinedRoomId(userId);
        boolean isHandsUp = participantRepository.toggleHandsUp(roomId, userId);

        return new HandsUpPayload(roomId, userId, isHandsUp);
    }
}
