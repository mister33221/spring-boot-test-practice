package com.kai.test_practice.services;

import com.kai.test_practice.entities.User;
import com.kai.test_practice.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Profile({"dev", "test"})
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testGetAllUsers() {
        List<User> users = userService.getAllUsers();
        assertEquals(2, users.size());
        assertEquals("user1", users.get(0).getName());
        assertEquals("user1@example.com", users.get(0).getEmail());
        assertEquals("user2", users.get(1).getName());
        assertEquals("user2@example.com", users.get(1).getEmail());
    }

//    @Test
//    public void testGetUserByIdNotFound() {
//        Exception exception = assertThrows(UserNoztFoundException.class, () -> {
//            userService.getUserById(99L);
//        });
//
//        assertEquals("User with ID 99 not found.", exception.getMessage());
//    }


}
