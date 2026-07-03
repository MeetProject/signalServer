package com.meetProject.signalserver.repository;

import com.meetProject.signalserver.domain.User;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> User.restore(
            rs.getString("id"),
            rs.getString("name"),
            rs.getString("profile_color"),
            rs.getTimestamp("created_at").toLocalDateTime()
    );

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(User user) {
        jdbcTemplate.update(
                "INSERT INTO member (id, name, profile_color, created_at) VALUES (?, ?, ?, ?)",
                user.getId(),
                user.getName(),
                user.getProfileColor(),
                user.getCreatedAt()
        );
    }

    public void deleteById(String userId) {
        jdbcTemplate.update("DELETE FROM member WHERE id = ?", userId);
    }

    public Optional<User> findById(String userId) {
        return jdbcTemplate.query("SELECT id, name, profile_color, created_at FROM member WHERE id = ?", USER_ROW_MAPPER, userId)
                .stream()
                .findFirst();
    }

    public boolean existsById(String userId) {
        Boolean exists = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM member WHERE id = ?)",
                Boolean.class, userId
        );
        return Boolean.TRUE.equals(exists);
    }
}
