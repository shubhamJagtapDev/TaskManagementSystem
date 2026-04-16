package repositories;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import models.User;

public class UserRepositoryImpl implements UserRepository {

    private Map<String, User> userMap;
    private long userCount;

    public UserRepositoryImpl() {
        userMap = new HashMap<>();
        userCount = 0;
    }

    @Override
    public Optional<User> findUserById(String userId) {
        if (!userMap.containsKey(userId))
            return Optional.empty();
        return Optional.of(userMap.get(userId));
    }

    @Override
    public User save(User user) {
        userCount += 1;
        String userId = "user_id_" + userCount;
        user.setUser_id(userId);
        userMap.put(userId, user);
        return user;
    }

}
