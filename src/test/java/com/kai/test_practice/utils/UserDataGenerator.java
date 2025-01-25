package com.kai.test_practice.utils;

import com.kai.test_practice.entities.User;

import java.util.ArrayList;
import java.util.List;

public class UserDataGenerator {

    public static List<User> generateUsers(int count) {
        List<User> users = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            users.add(User.builder()
                    .id((long) i)
                    .name("User" + i)
                    .email("user" + i + "@example.com")
                    .build());
        }
        return users;
    }

}
