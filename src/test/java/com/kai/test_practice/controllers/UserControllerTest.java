package com.kai.test_practice.controllers;

import com.kai.test_practice.entities.User;
import com.kai.test_practice.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Profile({"dev", "test"}) // 只在 dev 和 test Profile 下啟用
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

//    @Test
//    public void testGetAllUsers() throws Exception {
//
//    }

    @Test
    public void testCreateAndGetUsers() throws Exception {
        // 創建使用者
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content("{\"name\": \"Test User\", \"email\": \"test@example.com\"}"))
                .andDo(print()) // 打印 API 響應內容
                .andExpect(status().isOk());

        // 查詢所有使用者，而其中應該包含剛剛創建的使用者
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name").value(org.hamcrest.Matchers.hasItem("Test User")))
                .andExpect(jsonPath("$[*].email").value(org.hamcrest.Matchers.hasItem("test@example.com")));
    }

    @Test
    public void testGetUserByIdNotFound() throws Exception {
        mockMvc.perform(get("/users/999")) // 假設 ID 999 不存在
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"))
                .andExpect(jsonPath("$.message").value("User with ID 999 not found."));
    }

    @Test
    public void testCreateUserValidationError() throws Exception {
        // 缺少 email
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content("{\"name\": \"Invalid User\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.message").value("Email is required"));
    }
}
