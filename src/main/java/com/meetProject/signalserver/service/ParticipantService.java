package com.meetProject.signalserver.service;

import com.meetProject.signalserver.domain.RoomSession;
import com.meetProject.signalserver.dto.application.HandsUpPayload;
import com.meetProject.signalserver.dto.application.ProducerPayload;
import com.meetProject.signalserver.exception.ParticipantNotJoinedException;
import com.meetProject.signalserver.repository.RoomSessionRepository;
import org.springframework.stereotype.Service;

@Service
public class ParticipantService {
    private final RoomSessionRepository roomSessionRepository;

    public ParticipantService(RoomSessionRepository roomSessionRepository) {
        this.roomSessionRepository = roomSessionRepository;
    }

    public ProducerPayload registerProducer(String userId, String producerId) {
        RoomSession session = getSessionByUser(userId);
        session.addProducer(userId, producerId);

        return new ProducerPayload(session.getRoomId(), userId, producerId);
    }

    public ProducerPayload removeProducer(String userId, String producerId) {
        RoomSession session = getSessionByUser(userId);
        session.removeProducer(userId, producerId);

        return new ProducerPayload(session.getRoomId(), userId, producerId);
    }

    public HandsUpPayload toggleHandsUp(String userId) {
        RoomSession session = getSessionByUser(userId);
        boolean isHandsUp = session.toggleHandsUp(userId);

        return new HandsUpPayload(session.getRoomId(), userId, isHandsUp);
    }

    public String getJoinedRoomId(String userId) {
        return roomSessionRepository.findRoomIdByUserId(userId)
                .orElseThrow(ParticipantNotJoinedException::new);
    }

    private RoomSession getSessionByUser(String userId) {
        return roomSessionRepository.findByUserId(userId)
                .orElseThrow(ParticipantNotJoinedException::new);
    }
}
