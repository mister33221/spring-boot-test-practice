package com.kai.test_practice.services;

import com.kai.test_practice.entities.CreateUserRequest;
import com.kai.test_practice.entities.User;
import com.kai.test_practice.exceptions.UserNotFoundException;
import com.kai.test_practice.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    public synchronized  User createUser(CreateUserRequest userRequest) {
        // 檢查 email 是否已存在
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userRequest.getEmail());
        }

        User newUser = User.createUser(userRequest);
        return userRepository.save(newUser);
    }

    // 通過ID獲取使用者
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + id + " not found."));
    }

}
