package services;

import java.util.Optional;

import models.User;
import repositories.UserRepository;
import repositories.UserRepositoryImpl;

public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    public UserServiceImpl() {
        userRepository = new UserRepositoryImpl();
    }

    @Override
    public User onboardUser(String name, int quotaLimit) {
        User user = new User(name, quotaLimit);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUser(String userId) {
        return userRepository.findUserById(userId);
    }

}
