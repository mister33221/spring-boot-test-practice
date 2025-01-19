package com.kai.test_practice.services;

import com.kai.test_practice.entities.User;
import com.kai.test_practice.exceptions.UserNotFoundException;
import com.kai.test_practice.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {


    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 獲取所有使用者
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 創建新使用者
    public User createUser(User user) {
        user.setCode("USER-" + System.currentTimeMillis());
        return userRepository.save(user);
    }

    // 通過ID獲取使用者
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found."));
    }

}
