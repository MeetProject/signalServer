package com.meetProject.signalserver.repository;

import com.meetProject.signalserver.domain.Room;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class RoomRepository {
    private static final RowMapper<Room> ROOM_ROW_MAPPER = (rs, rowNum) ->
            Room.restore(rs.getString("id"), rs.getTimestamp("created_at").toLocalDateTime());

    private final JdbcTemplate jdbcTemplate;

    public RoomRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Room room) {
        jdbcTemplate.update(
                "INSERT INTO room (id, created_at) VALUES (?, ?)",
                room.getId(), room.getCreatedAt()
        );
    }

    public Optional<Room> findById(String roomId) {
        return jdbcTemplate.query("SELECT id, created_at FROM room WHERE id = ?", ROOM_ROW_MAPPER, roomId)
                .stream()
                .findFirst();
    }

    public boolean existsById(String roomId) {
        Boolean exists = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM room WHERE id = ?)",
                Boolean.class, roomId
        );
        return Boolean.TRUE.equals(exists);
    }

    public void deleteById(String roomId) {
        jdbcTemplate.update("DELETE FROM room WHERE id = ?", roomId);
    }
}
