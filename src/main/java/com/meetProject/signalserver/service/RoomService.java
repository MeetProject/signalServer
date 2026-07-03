package com.meetProject.signalserver.service;

import com.meetProject.signalserver.domain.Participant;
import com.meetProject.signalserver.domain.Room;
import com.meetProject.signalserver.domain.RoomSession;
import com.meetProject.signalserver.domain.User;
import com.meetProject.signalserver.dto.application.ParticipantPayload;
import com.meetProject.signalserver.dto.application.ResyncPayload;
import com.meetProject.signalserver.dto.socket.ParticipantDto;
import com.meetProject.signalserver.dto.rest.RoomSessionDto.CreateRoomResponse;
import com.meetProject.signalserver.dto.rest.RoomSessionDto.ValidateRoomResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.JoinRequest;
import com.meetProject.signalserver.exception.RoomNotFoundException;
import com.meetProject.signalserver.exception.UserNotFoundException;
import com.meetProject.signalserver.repository.RoomRepository;
import com.meetProject.signalserver.repository.RoomSessionRepository;
import com.meetProject.signalserver.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomSessionRepository roomSessionRepository;

    public RoomService(RoomRepository roomRepository, UserRepository userRepository, RoomSessionRepository roomSessionRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.roomSessionRepository = roomSessionRepository;
    }

    @Retryable(retryFor = DuplicateKeyException.class)
    public CreateRoomResponse save() {
        Room room = Room.create();
        roomRepository.save(room);
        return new CreateRoomResponse(room.getId());
    }

    public ValidateRoomResponse validate(String roomId) {
        boolean roomExists = roomRepository.existsById(roomId);
        boolean roomFull = roomSessionRepository.find(roomId)
                .map(RoomSession::isFull)
                .orElse(false);

        return new ValidateRoomResponse(roomExists && !roomFull);
    }

    public ParticipantPayload join(String userId, JoinRequest joinRequest) {
        User user = getUser(userId);

        if(!roomRepository.existsById(joinRequest.roomId())) {
            throw new RoomNotFoundException();
        }

        Participant joiner = joinRequest.to(user);
        List<Participant> others = roomSessionRepository.join(joinRequest.roomId(), joiner);

        return new ParticipantPayload(ParticipantDto.from(joiner), toDto(others));
    }

    public ResyncPayload resync(String userId) {
        return roomSessionRepository.findByUserId(userId)
                .map(session -> new ResyncPayload(toDto(session.participants()), false))
                .orElseGet(() -> new ResyncPayload(List.of(), true));
    }

    public Optional<String> leave(String userId) {
        Optional<String> roomId = roomSessionRepository.leave(userId);
        roomId.filter(id -> roomSessionRepository.find(id).isEmpty())
                .ifPresent(roomRepository::deleteById);

        return roomId;
    }

    private User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private List<ParticipantDto> toDto(List<Participant> participants) {
        return participants.stream()
                .map(ParticipantDto::from)
                .toList();
    }

}
