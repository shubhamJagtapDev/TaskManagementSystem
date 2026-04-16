package repositories;

import java.util.Optional;

import models.User;

public interface UserRepository {

    Optional<User> findUserById(String userId);

    User save(User user);

}