package com.meetProject.signalserver.service;

import com.meetProject.signalserver.constant.RoomRule;
import com.meetProject.signalserver.domain.Participant;
import com.meetProject.signalserver.domain.Room;
import com.meetProject.signalserver.domain.User;
import com.meetProject.signalserver.dto.application.ParticipantPayload;
import com.meetProject.signalserver.dto.rest.RoomSessionDto.CreateRoomResponse;
import com.meetProject.signalserver.dto.rest.RoomSessionDto.ValidateRoomResponse;
import com.meetProject.signalserver.dto.socket.RoomSessionDto.JoinRequest;
import com.meetProject.signalserver.exception.RoomFullException;
import com.meetProject.signalserver.exception.RoomNotFoundException;
import com.meetProject.signalserver.exception.UserNotFoundException;
import com.meetProject.signalserver.repository.ParticipantRepository;
import com.meetProject.signalserver.repository.RoomRepository;
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
    private final ParticipantRepository participantRepository;

    public RoomService(RoomRepository roomRepository, UserRepository userRepository, ParticipantRepository participantRepository) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.participantRepository = participantRepository;
    }

    @Retryable(retryFor = DuplicateKeyException.class)
    public CreateRoomResponse save() {
        Room room = Room.create();
        roomRepository.save(room);
        return new CreateRoomResponse(room.getId());
    }

    public ValidateRoomResponse validate(String roomId) {
        boolean isExistRoom = roomRepository.existsById(roomId);
        boolean isFull = participantRepository.countByRoom(roomId) >= RoomRule.MAX_ROOM_PARTICIPANTS;

        return new ValidateRoomResponse(isExistRoom && !isFull);
    }

    public ParticipantPayload join(String userId, JoinRequest joinRequest) {
        User user = getUser(userId);

        if(!roomRepository.existsById(joinRequest.roomId())) {
            throw new RoomNotFoundException();
        }

        if(participantRepository.countByRoom(joinRequest.roomId()) >= RoomRule.MAX_ROOM_PARTICIPANTS) {
            throw new RoomFullException();
        }

        Participant joiner = joinRequest.to(user);
        List<Participant> others = participantRepository.add(joinRequest.roomId(), joiner);

        return new ParticipantPayload(joiner, others);
    }

    public Optional<List<Participant>> resync(String userId) {
        return participantRepository.findRoomIdByUserId(userId)
                .map(roomId -> participantRepository.findByRoom(roomId).orElse(List.of()));
    }

    public Optional<String> leave(String userId) {
        Optional<String> roomId = participantRepository.findRoomIdByUserId(userId);
        roomId.ifPresent(id -> {
            participantRepository.remove(id, userId);

            if(participantRepository.countByRoom(id) == 0) {
                roomRepository.deleteById(id);
            }
        });

        return roomId;
    }

    private User getUser(String userId) {
        return userRepository.findById(userId)
                .stream()
                .findFirst()
                .orElseThrow(UserNotFoundException::new);
    }

}
