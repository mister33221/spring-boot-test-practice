package com.kai.test_practice.initializers;

import com.kai.test_practice.entities.User;
import com.kai.test_practice.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "test"}) // 只在 dev 和 test Profile 下啟用
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public void run(String... args) throws Exception {
        System.out.println("Initializing data...");
        userRepository.save(User.builder().name("user1").email("user1@example.com").build());
        userRepository.save(User.builder().name("user2").email("user2@example.com").build());
        System.out.println("Data initialized.");    }
}
