package com.kai.test_practice.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/sql/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
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

    //    為什麼在h2 db 中並沒有看到真實的資料被建立，但我使用repository 去查，確實db裡面有資料?
    @ParameterizedTest
    @MethodSource("provideCreateUserData")
    public void testCreateUserWithMethodSource(String name, String email, String errorType) throws Exception {
        // 創建使用者請求的 JSON 格式
        var request = "{\"name\": \"" + name + "\", \"email\": \"" + email + "\"}";

        switch (errorType) {
            case "invalid":
                mockMvc.perform(post("/users")
                                .contentType("application/json")
                                .content(request))
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.error").value("Validation failed"));
                break;
            case "duplicate":
                mockMvc.perform(post("/users")
                                .contentType("application/json")
                                .content(request))
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message").value("Email already exists: " + email));
                break;

            case "work": // 有效數據
                mockMvc.perform(post("/users")
                                .contentType("application/json")
                                .content(request))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name").value(name))
                        .andExpect(jsonPath("$.email").value(email));
                break;
            default:
                throw new IllegalArgumentException("Invalid error type: " + errorType);
        }
    }
    private static Stream<Arguments> provideCreateUserData() {
        return Stream.of(
                Arguments.of("John", "john@example.com", "work"),   // 有效數據
                Arguments.of("A", "short@example.com", "invalid"),      // 名字太短
                Arguments.of("John", "invalidemail@", "invalid"),       // 無效 Email
                Arguments.of("John", "john.doe@example.com", "duplicate")     // 重複 Email
        );
    }

    @ParameterizedTest
    @CsvSource({
            "John, john@example.com, work",          // 有效數據
            "A, short@example.com, invalid",         // 名字太短
            "John, invalidemail@, invalid",          // 無效 Email
            "John, john.doe@example.com, duplicate"  // 重複 Email
    })
    public void testCreateUserWithCsvSource(String name, String email, String errorType) throws Exception {
        // 創建使用者請求的 JSON 格式
        var request = "{\"name\": \"" + name + "\", \"email\": \"" + email + "\"}";

        switch (errorType) {
            case "invalid":
                mockMvc.perform(post("/users")
                                .contentType("application/json")
                                .content(request))
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.error").value("Validation failed"));
                break;

            case "duplicate":
                mockMvc.perform(post("/users")
                                .contentType("application/json")
                                .content(request))
                        .andDo(print())
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.message").value("Email already exists: " + email));
                break;

            case "work": // 有效數據
                mockMvc.perform(post("/users")
                                .contentType("application/json")
                                .content(request))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name").value(name))
                        .andExpect(jsonPath("$.email").value(email));
                break;

            default:
                throw new IllegalArgumentException("Invalid error type: " + errorType);
        }
    }
}
