package com.meetProject.signalserver.fake;

import com.meetProject.signalserver.domain.Room;
import com.meetProject.signalserver.repository.RoomRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeRoomRepository extends RoomRepository {
    private final Map<String, Room> store = new HashMap<>();

    public FakeRoomRepository() {
        super(null);
    }

    @Override
    public void save(Room room) {
        store.put(room.getId(), room);
    }

    @Override
    public Optional<Room> findById(String roomId) {
        return Optional.ofNullable(store.get(roomId));
    }

    @Override
    public boolean existsById(String roomId) {
        return store.containsKey(roomId);
    }

    @Override
    public void deleteById(String roomId) {
        store.remove(roomId);
    }
}
