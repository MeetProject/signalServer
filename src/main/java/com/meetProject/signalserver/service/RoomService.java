package com.meetProject.signalserver.service;

import com.meetProject.signalserver.domain.MediaOption;
import com.meetProject.signalserver.domain.Participant;
import com.meetProject.signalserver.domain.Room;
import com.meetProject.signalserver.domain.RoomSession;
import com.meetProject.signalserver.domain.User;
import com.meetProject.signalserver.dto.application.JoinResult;
import com.meetProject.signalserver.dto.application.ResyncResult;
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
    public String save() {
        Room room = Room.create();
        roomRepository.save(room);
        return room.getId();
    }

    public boolean validate(String roomId) {
        boolean roomExists = roomRepository.existsById(roomId);
        boolean roomFull = roomSessionRepository.find(roomId)
                .map(RoomSession::isFull)
                .orElse(false);

        return roomExists && !roomFull;
    }

    public JoinResult join(String userId, String roomId, MediaOption mediaOption) {
        User user = getUser(userId);

        if (!roomRepository.existsById(roomId)) {
            throw new RoomNotFoundException();
        }

        Participant joiner = Participant.join(user, mediaOption);
        List<Participant> others = roomSessionRepository.join(roomId, joiner);

        return new JoinResult(joiner, others);
    }

    public ResyncResult resync(String userId) {
        return roomSessionRepository.findByUserId(userId)
                .map(session -> new ResyncResult(session.participants(), false))
                .orElseGet(() -> new ResyncResult(List.of(), true));
    }

    public Optional<String> leave(String userId) {
        return roomSessionRepository.leave(userId)
                .map(result -> {
                    if (result.roomEmpty()) {
                        roomRepository.deleteById(result.roomId());
                    }
                    return result.roomId();
                });
    }

    private User getUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}
