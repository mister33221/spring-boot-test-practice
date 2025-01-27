package com.kai.test_practice.repositories;


import com.kai.test_practice.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest // @SpringBootTest 會加載整個 Spring 上下文，包括所有的 @Component 和 @Service，確保 DataInitializer 被執行。
//@DataJpaTest // @DataJpaTest 不會自動加載像 DataInitializer 這樣的 @Component
@Sql(scripts = "/sql/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindAll() {
        List<User> users = userRepository.findAll();
        assertEquals(2, users.size());
        assertEquals("John Doe", users.get(0).getName());
        assertEquals("john.doe@example.com", users.get(0).getEmail());
        assertEquals("Jane Smith", users.get(1).getName());
        assertEquals("jane.smith@example.com", users.get(1).getEmail());
    }

}
