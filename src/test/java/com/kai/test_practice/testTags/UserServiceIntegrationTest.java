package com.kai.test_practice.testTags;

import com.kai.test_practice.entities.User;
import com.kai.test_practice.services.UserService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@Tag("integration")
@SpringBootTest
@Sql(scripts = "/sql/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserServiceIntegrationTest {

//    @Autowired
//    private UserService userService;
//
//    @Test
//    public void testGetAllUsers() {
//        List<User> users = userService.getAllUsers();
//        assertEquals(2, users.size());
//    }
//
//    @Test
//    public void testGetUserById() {
//        User user = userService.getUserById(1L);
//        assertEquals("John Doe", user.getName());
//    }
}
