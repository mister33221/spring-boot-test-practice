package com.kai.test_practice.e2eRestTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserApiTest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    public void testUserLifecycle() throws Exception {
//        // Step 1: 創建新使用者
//        mockMvc.perform(post("/users")
//                        .contentType("application/json")
//                        .content("{\"name\": \"Test User\", \"email\": \"test@example.com\"}"))
//                .andExpect(status().isOk());
//
//        // Step 2: 查詢剛創建的使用者
//        mockMvc.perform(get("/users"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].name").value("Test User"))
//                .andExpect(jsonPath("$[0].email").value("test@example.com"));
//    }

}
