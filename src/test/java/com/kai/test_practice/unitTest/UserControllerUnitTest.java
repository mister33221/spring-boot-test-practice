package com.kai.test_practice.unitTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kai.test_practice.entities.CreateUserRequest;
import com.kai.test_practice.entities.User;
import com.kai.test_practice.repositories.UserRepository;
import com.kai.test_practice.services.UserService;
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

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/sql/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService mockSserService;

//    Unit test
    @Test
    public void testGetUserById() throws Exception {
        User mockUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .code("123")
                .build();
        Mockito.when(mockSserService.getUserById(1L)).thenReturn(mockUser);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.code").value("123"));
    }

//    Unit test
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

//    Unit test
    @Test
    public void testGetAllUsers() throws Exception {
        List<User> mockUsers = Arrays.asList(
                User.builder().id(1L).name("John Doe").email("john.doe@example.com").code("123").build(),
                User.builder().id(2L).name("Jane Smith").email("jane.smith@example.com").code("456").build()
        );
        Mockito.when(mockSserService.getAllUsers()).thenReturn(mockUsers);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Jane Smith"));
    }

//    Unit test
    @Test
    public void testCreateUser() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest("John Doe", "john.doe@example.com");
        User mockUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .code("123")
                .build();
        Mockito.when(mockSserService.createUser(Mockito.any(CreateUserRequest.class))).thenReturn(mockUser);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(createUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.code").value("123"));
    }


}
