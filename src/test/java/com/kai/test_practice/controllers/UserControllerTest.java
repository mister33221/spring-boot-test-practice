package com.kai.test_practice.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Profile({"dev", "test"}) // 只在 dev 和 test Profile 下啟用
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("user1"))
                .andExpect(jsonPath("$[0].email").value("user1@example.com"))
                .andExpect(jsonPath("$[1].name").value("user2"))
                .andExpect(jsonPath("$[1].email").value("user2@example.com"));
    }

//    @Test
//    public void testCreateAndGetUsers() throws Exception {
//        // 創建使用者
//        mockMvc.perform(post("/users")
//                        .contentType("application/json")
//                        .content("{\"name\": \"Test User\", \"email\": \"test@example.com\"}"))
//                .andExpect(status().isOk());
//
//        // 查詢所有使用者
//        mockMvc.perform(get("/users"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].name").value("Test User"))
//                .andExpect(jsonPath("$[0].email").value("test@example.com"));
//    }
}
