package com.kai.test_practice.integrationTest;

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

@SpringBootTest // 標註為 Spring Boot 測試類，啟動完整的 Spring 應用上下文進行測試，適用於整合測試。
@AutoConfigureMockMvc // 自動配置 MockMvc，用於模擬 HTTP 請求和回應，無需啟動真實的伺服器。
@Sql(scripts = "/sql/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD) // 在每個測試方法之前執行 SQL 腳本，初始化測試數據。
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)  // 在每個測試方法之後執行 SQL 腳本，清理測試數據。
public class UserControllerIntegrationTest {

    @Autowired // 自動注入 MockMvc 實例，用於模擬 HTTP 請求和回應。
    private MockMvc mockMvc;

    @Test // 標註這是一個測試方法，由 JUnit 運行。
    public void testCreateAndGetUsers() throws Exception {
        // 創建使用者
        mockMvc.perform(post("/users") // 模擬對 "/users" 的 POST 請求，用於創建使用者。
                        .contentType("application/json") // 設置請求內容類型為 JSON。
                        .content("{\"name\": \"Test User\", \"email\": \"test@example.com\"}")) // 提供 JSON 格式的請求體。
                .andDo(print()) // 打印請求與回應的詳細內容，用於調試。
                .andExpect(status().isCreated()); // 驗證返回的 HTTP 狀態碼是否為 201 (Created)。

        // 查詢所有使用者，驗證創建的使用者是否存在
        mockMvc.perform(get("/users")) // 模擬對 "/users" 的 GET 請求，用於查詢所有使用者。
                .andExpect(status().isOk()) // 驗證返回的 HTTP 狀態碼是否為 200 (OK)。
                .andExpect(jsonPath("$[*].name").value(org.hamcrest.Matchers.hasItem("Test User"))) // 驗證返回的 JSON 包含指定的 name。
                .andExpect(jsonPath("$[*].email").value(org.hamcrest.Matchers.hasItem("test@example.com"))); // 驗證返回的 JSON 包含指定的 email。
    }

    @ParameterizedTest // 標註這是一個參數化測試方法，允許使用多組測試數據重複執行測試。
    @MethodSource("provideCreateUserData") // 指定測試數據來自名為 "provideCreateUserData" 的靜態方法。
    public void testCreateUserWithMethodSource(String name, String email, String errorType) throws Exception {
        var request = "{\"name\": \"" + name + "\", \"email\": \"" + email + "\"}"; // 創建 JSON 格式的請求數據。

        switch (errorType) {
            case "invalid": // 測試無效數據
                mockMvc.perform(post("/users")
                                .contentType("application/json")
                                .content(request))
                        .andDo(print())
                        .andExpect(status().isBadRequest()) // 驗證返回狀態碼為 400 (Bad Request)。
                        .andExpect(jsonPath("$.error").value("Validation failed")); // 驗證錯誤訊息。
                break;

            case "duplicate": // 測試重複數據
                mockMvc.perform(post("/users")
                                .contentType("application/json")
                                .content(request))
                        .andDo(print())
                        .andExpect(status().isBadRequest()) // 驗證返回狀態碼為 400。
                        .andExpect(jsonPath("$.message").value("Email already exists: " + email)); // 驗證錯誤訊息。
                break;

            case "work": // 測試有效數據
                mockMvc.perform(post("/users")
                                .contentType("application/json")
                                .content(request))
                        .andDo(print())
                        .andExpect(status().isCreated()) // 驗證返回狀態碼為 201 (Created)。
                        .andExpect(jsonPath("$.name").value(name)) // 驗證返回的 JSON 包含指定的 name。
                        .andExpect(jsonPath("$.email").value(email)); // 驗證返回的 JSON 包含指定的 email。
                break;

            default:
                throw new IllegalArgumentException("Invalid error type: " + errorType); // 對於未定義的錯誤類型，拋出異常。
        }
    }

    private static Stream<Arguments> provideCreateUserData() {
        // 提供參數化測試數據，每組數據包含 name、email 和 errorType。
        return Stream.of(
                Arguments.of("John", "john@example.com", "work"), // 有效數據
                Arguments.of("A", "short@example.com", "invalid"), // 名字太短
                Arguments.of("John", "invalidemail@", "invalid"), // 無效 Email
                Arguments.of("John", "john.doe@example.com", "duplicate") // 重複 Email
        );
    }

    @ParameterizedTest // 標註這是一個參數化測試方法。
    @CsvSource({ // 使用 CSV 格式提供參數化測試數據。
            "John, john@example.com, work",          // 有效數據
            "A, short@example.com, invalid",         // 名字太短
            "John, invalidemail@, invalid",          // 無效 Email
            "John, john.doe@example.com, duplicate"  // 重複 Email
    })
    public void testCreateUserWithCsvSource(String name, String email, String errorType) throws Exception {
        var request = "{\"name\": \"" + name + "\", \"email\": \"" + email + "\"}"; // 創建 JSON 格式的請求數據。

        switch (errorType) {
            case "invalid": // 測試無效數據
                mockMvc.perform(post("/users")
                                .contentType("application/json")
                                .content(request))
                        .andDo(print())
                        .andExpect(status().isBadRequest()) // 驗證返回狀態碼為 400。
                        .andExpect(jsonPath("$.error").value("Validation failed")); // 驗證錯誤訊息。
                break;

            case "duplicate": // 測試重複數據
                mockMvc.perform(post("/users")
                                .contentType("application/json")
                                .content(request))
                        .andDo(print())
                        .andExpect(status().isBadRequest()) // 驗證返回狀態碼為 400。
                        .andExpect(jsonPath("$.message").value("Email already exists: " + email)); // 驗證錯誤訊息。
                break;

            case "work": // 測試有效數據
                mockMvc.perform(post("/users")
                                .contentType("application/json")
                                .content(request))
                        .andDo(print())
                        .andExpect(status().isCreated()) // 驗證返回狀態碼為 201 (Created)。
                        .andExpect(jsonPath("$.name").value(name)) // 驗證返回的 JSON 包含指定的 name。
                        .andExpect(jsonPath("$.email").value(email)); // 驗證返回的 JSON 包含指定的 email。
                break;

            default:
                throw new IllegalArgumentException("Invalid error type: " + errorType); // 對於未定義的錯誤類型，拋出異常。
        }
    }
}
