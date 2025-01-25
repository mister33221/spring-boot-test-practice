package com.kai.test_practice.services;

import com.kai.test_practice.entities.CreateUserRequest;
import com.kai.test_practice.entities.User;
import com.kai.test_practice.exceptions.UserNotFoundException;
import com.kai.test_practice.repositories.UserRepository;
import com.kai.test_practice.utils.UserDataGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@SpringBootTest
@Profile({"dev", "test"})
@Sql(scripts = "/sql/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserServiceTest {

    private final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private final UserService userService = new UserService(userRepository);

    private Set<String> emailSet; // 用於模擬資料庫的 email 集合

    @BeforeEach
    public void setup() {
        emailSet = new HashSet<>();

        // 模擬 existsByEmail 行為
        when(userRepository.existsByEmail(anyString())).thenAnswer(invocation -> {
            String email = invocation.getArgument(0);
            return emailSet.contains(email); // 判斷 email 是否已存在
        });

        // 模擬 save 行為
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            if (!emailSet.add(user.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + user.getEmail());
            }
            return user;
        });
    }

    @Test
    public void testUserNotFoundException() {
        // 模擬找不到使用者
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // 驗證是否拋出 UserNotFoundException
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(999L);
        });
        assertEquals("User with ID 999 not found.", exception.getMessage());
    }

    @Test
    public void testCreateUserEmailAlreadyExists() {
        // 模擬 email 已存在
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // 驗證是否拋出 IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            CreateUserRequest request = new CreateUserRequest();
            request.setName("Test User");
            request.setEmail("existing@example.com");
            userService.createUser(request);
        });
        assertEquals("Email already exists: existing@example.com", exception.getMessage());
    }

    @Test
    public void testCreateUserInvalidNameLength() {
        // 驗證 name 長度過短
        IllegalArgumentException shortNameException = assertThrows(IllegalArgumentException.class, () -> {
            CreateUserRequest request = new CreateUserRequest();
            request.setName("ab");
            request.setEmail("valid@example.com");
            userService.createUser(request);
        });
        assertEquals("Name length must be between 3 and 50 characters", shortNameException.getMessage());

        // 驗證 name 長度過長
        IllegalArgumentException longNameException = assertThrows(IllegalArgumentException.class, () -> {
            CreateUserRequest request = new CreateUserRequest();
            request.setName("a".repeat(51));
            request.setEmail("valid@example.com");
            userService.createUser(request);
        });
        assertEquals("Name length must be between 3 and 50 characters", longNameException.getMessage());
    }

    @Test
    public void testGetAllUsersWithLargeDataset() {
        // 模擬生成 10000 條數據
        List<User> mockUsers = UserDataGenerator.generateUsers(10000);

        // 模擬 Repository 返回這些數據
        when(userRepository.findAll()).thenReturn(mockUsers);

        // 測試 Service 層邏輯
        List<User> result = userService.getAllUsers();

        // 驗證數據量與結果一致
        assertEquals(10000, result.size());
        assertEquals("User1", result.get(0).getName());
        assertEquals("user1@example.com", result.get(0).getEmail());

        // 確保 Repository 方法被調用了一次
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void testCreateUserConcurrency() throws InterruptedException {
        int threadCount = 10; // 模擬 10 個併發請求
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                CreateUserRequest request = new CreateUserRequest();
                request.setName("User");
                request.setEmail("user@example.com"); // 模擬多個執行緒使用相同 email

                try {
                    userService.createUser(request);
                } catch (Exception ignored) {
                    // 忽略異常
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);

        // 驗證 save 方法只被成功調用一次
        verify(userRepository, times(1)).save(any(User.class));
    }

}
