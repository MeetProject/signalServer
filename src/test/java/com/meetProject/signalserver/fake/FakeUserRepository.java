package com.meetProject.signalserver.fake;

import com.meetProject.signalserver.domain.User;
import com.meetProject.signalserver.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FakeUserRepository extends UserRepository {
    private final Map<String, User> store = new HashMap<>();

    public FakeUserRepository() {
        super(null);
    }

    @Override
    public void save(User user) {
        store.put(user.getId(), user);
    }

    @Override
    public void deleteById(String userId) {
        store.remove(userId);
    }

    @Override
    public Optional<User> findById(String userId) {
        return Optional.ofNullable(store.get(userId));
    }

    @Override
    public boolean existsById(String userId) {
        return store.containsKey(userId);
    }
}
