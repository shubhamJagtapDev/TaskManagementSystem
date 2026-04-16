package services;

import java.util.Optional;

import models.User;

public interface UserService {

    User onboardUser(String name, int quotaLimit);

    Optional<User> getUser(String userId);

}
