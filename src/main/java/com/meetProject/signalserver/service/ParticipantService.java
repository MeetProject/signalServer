package com.meetProject.signalserver.service;

import com.meetProject.signalserver.domain.MediaOption;
import com.meetProject.signalserver.domain.RoomSession;
import com.meetProject.signalserver.dto.application.DeviceResult;
import com.meetProject.signalserver.dto.application.HandsUpResult;
import com.meetProject.signalserver.dto.application.ProducerResult;
import com.meetProject.signalserver.exception.ParticipantNotJoinedException;
import com.meetProject.signalserver.repository.RoomSessionRepository;
import org.springframework.stereotype.Service;

@Service
public class ParticipantService {
    private final RoomSessionRepository roomSessionRepository;

    public ParticipantService(RoomSessionRepository roomSessionRepository) {
        this.roomSessionRepository = roomSessionRepository;
    }

    public ProducerResult registerProducer(String userId, String producerId) {
        RoomSession session = getSessionByUser(userId);
        session.addProducer(userId, producerId);

        return new ProducerResult(session.getRoomId(), userId, producerId);
    }

    public ProducerResult removeProducer(String userId, String producerId) {
        RoomSession session = getSessionByUser(userId);
        session.removeProducer(userId, producerId);

        return new ProducerResult(session.getRoomId(), userId, producerId);
    }

    public HandsUpResult toggleHandsUp(String userId) {
        RoomSession session = getSessionByUser(userId);
        boolean isHandsUp = session.toggleHandsUp(userId);

        return new HandsUpResult(session.getRoomId(), userId, isHandsUp);
    }

    public DeviceResult updateDevice(String userId, MediaOption mediaOption) {
        RoomSession session = getSessionByUser(userId);
        session.updateMediaOption(userId, mediaOption);

        return new DeviceResult(session.getRoomId(), userId, mediaOption);
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
