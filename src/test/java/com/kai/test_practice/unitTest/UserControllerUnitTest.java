package com.kai.test_practice.unitTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kai.test_practice.entities.CreateUserRequest;
import com.kai.test_practice.entities.User;
import com.kai.test_practice.repositories.UserRepository;
import com.kai.test_practice.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest // 標註這是一個 Spring Boot 測試類，啟動完整的 Spring 應用上下文進行測試，適用於整合測試和模擬環境。
@AutoConfigureMockMvc // 自動配置 MockMvc，用於模擬 HTTP 請求和回應，無需啟動真實的伺服器。
public class UserControllerUnitTest {

    @Autowired // Spring 自動注入 MockMvc 實例，用於發送模擬的 HTTP 請求。
    private MockMvc mockMvc;

//    @MockBean // 注意這個 Annotation 已經在 SpringBoot 3.4.0 版本中被棄用，改用 @MockitoBean
    @MockitoBean// 使用 Mockito 模擬一個 Service 層的 Bean，將其注入到 Spring 測試上下文中。
    private UserService mockUserService;

    private User mockUser; // 用於測試的模擬數據，代表一個單一的 User 實例。
    private List<User> mockUsers; // 用於測試的模擬數據列表，代表多個 User 實例。

    @BeforeEach // 在每個測試方法執行之前執行，用於初始化測試所需的數據或狀態。
    public void setUp() {
        // 初始化單個模擬使用者
        mockUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .code("123")
                .build();

        // 初始化模擬使用者列表
        mockUsers = Arrays.asList(
                mockUser,
                User.builder().id(2L).name("Jane Smith").email("jane.smith@example.com").code("456").build()
        );

        // 使用 Mockito 模擬 Service 層的行為
        Mockito.when(mockUserService.getUserById(1L)).thenReturn(mockUser);
        Mockito.when(mockUserService.getAllUsers()).thenReturn(mockUsers);
    }

    @AfterEach // 在每個測試方法執行之後執行，用於清理測試環境或重置狀態。
    public void tearDown() {
        Mockito.reset(mockUserService); // 重置模擬對象，清除所有之前的行為模擬，確保不影響其他測試。
    }

    @Test // 標註這是一個測試方法，JUnit 會執行該方法進行測試。
    public void testGetUserById() throws Exception {
        // 構造一個模擬的使用者
        User mockUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .code("123")
                .build();

        // 模擬 Service 層的行為
        Mockito.when(mockUserService.getUserById(1L)).thenReturn(mockUser);

        // 使用 MockMvc 發送 GET 請求，並驗證返回結果
        mockMvc.perform(get("/users/1")) // 模擬對 "/users/1" 的 GET 請求
                .andExpect(status().isOk()) // 驗證返回狀態碼為 200 (OK)
                .andExpect(jsonPath("$.id").value(1L)) // 驗證 JSON 回應中的 id 為 1
                .andExpect(jsonPath("$.name").value("John Doe")) // 驗證 JSON 回應中的 name 為 "John Doe"
                .andExpect(jsonPath("$.email").value("john.doe@example.com")) // 驗證 JSON 回應中的 email 為 "john.doe@example.com"
                .andExpect(jsonPath("$.code").value("123")); // 驗證 JSON 回應中的 code 為 "123"
    }

    @Test // 標註這是一個測試方法，測試創建使用者時的輸入驗證。
    public void testCreateUserValidationError() throws Exception {
        // 使用 MockMvc 發送 POST 請求，測試缺少 email 時的錯誤
        mockMvc.perform(post("/users") // 模擬對 "/users" 的 POST 請求
                        .contentType("application/json") // 設定請求內容類型為 JSON
                        .content("{\"name\": \"Invalid User\"}")) // 傳遞不完整的 JSON 請求體
                .andExpect(status().isBadRequest()) // 驗證返回狀態碼為 400 (Bad Request)
                .andExpect(jsonPath("$.error").value("Validation failed")) // 驗證錯誤訊息
                .andExpect(jsonPath("$.message").value("Email is required")); // 驗證具體的錯誤內容
    }

    @Test // 標註這是一個測試方法，用於測試獲取所有使用者的功能。
    public void testGetAllUsers() throws Exception {
        // 準備模擬的使用者列表
        List<User> mockUsers = Arrays.asList(
                User.builder().id(1L).name("John Doe").email("john.doe@example.com").code("123").build(),
                User.builder().id(2L).name("Jane Smith").email("jane.smith@example.com").code("456").build()
        );

        // 模擬 Service 層的行為
        Mockito.when(mockUserService.getAllUsers()).thenReturn(mockUsers);

        // 使用 MockMvc 發送 GET 請求，並驗證返回結果
        mockMvc.perform(get("/users")) // 模擬對 "/users" 的 GET 請求
                .andExpect(status().isOk()) // 驗證返回狀態碼為 200 (OK)
                .andExpect(jsonPath("$[0].id").value(1L)) // 驗證第一個使用者的 id 為 1
                .andExpect(jsonPath("$[0].name").value("John Doe")) // 驗證第一個使用者的 name 為 "John Doe"
                .andExpect(jsonPath("$[1].id").value(2L)) // 驗證第二個使用者的 id 為 2
                .andExpect(jsonPath("$[1].name").value("Jane Smith")); // 驗證第二個使用者的 name 為 "Jane Smith"
    }

    @Test // 標註這是一個測試方法，用於測試創建新使用者的功能。
    public void testCreateUser() throws Exception {
        // 構造請求和回應對象
        CreateUserRequest createUserRequest = new CreateUserRequest("John Doe", "john.doe@example.com");
        User mockUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .code("123")
                .build();

        // 模擬 Service 層的行為
        Mockito.when(mockUserService.createUser(Mockito.any(CreateUserRequest.class))).thenReturn(mockUser);

        // 使用 MockMvc 發送 POST 請求，並驗證返回結果
        mockMvc.perform(post("/users") // 模擬對 "/users" 的 POST 請求
                        .contentType(MediaType.APPLICATION_JSON) // 設定請求內容類型為 JSON
                        .content(new ObjectMapper().writeValueAsString(createUserRequest))) // 傳遞 JSON 請求體
                .andExpect(status().isCreated()) // 驗證返回狀態碼為 201 (Created)
                .andExpect(jsonPath("$.id").value(1L)) // 驗證 JSON 回應中的 id 為 1
                .andExpect(jsonPath("$.name").value("John Doe")) // 驗證 JSON 回應中的 name 為 "John Doe"
                .andExpect(jsonPath("$.email").value("john.doe@example.com")) // 驗證 JSON 回應中的 email 為 "john.doe@example.com"
                .andExpect(jsonPath("$.code").value("123")); // 驗證 JSON 回應中的 code 為 "123"
    }
}
