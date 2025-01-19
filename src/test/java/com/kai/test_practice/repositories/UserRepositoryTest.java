package com.kai.test_practice.repositories;

import com.kai.test_practice.entities.User;
import com.kai.test_practice.initializers.DataInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest // @SpringBootTest 會加載整個 Spring 上下文，包括所有的 @Component 和 @Service，確保 DataInitializer 被執行。
//@DataJpaTest // @DataJpaTest 不會自動加載像 DataInitializer 這樣的 @Component
@Profile({"dev", "test"})
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

//    @DataJpaTest 不會自動加載像 DataInitializer 這樣的 @Component，因此需要在測試類中顯式定義它。
//    @TestConfiguration
//    static class TestConfig {
//        @Bean
//        public DataInitializer dataInitializer(UserRepository userRepository) {
//            return new DataInitializer(userRepository);
//        }
//    }

    @Test
    public void testFindAll() {
        List<User> users = userRepository.findAll();
        assertEquals(2, users.size());
        assertEquals("user1", users.get(0).getName());
        assertEquals("user1@example.com", users.get(0).getEmail());
        assertEquals("user2", users.get(1).getName());
        assertEquals("user2@example.com", users.get(1).getEmail());
    }

//    @BeforeEach
//    public void setup() {
//        userRepository.save(User.builder().name("Alice").email("alice@example.com").build());
//        userRepository.save(User.builder().name("Bob").email("bob@example.com").build());
//    }
//
//    @Test
//    public void testFindByNameAndEmail() {
//        List<User> users = userRepository.findByNameAndEmail("Alice", null);
//        assertEquals(1, users.size());
//        assertEquals("Alice", users.get(0).getName());
//
//        users = userRepository.findByNameAndEmail(null, "bob@example.com");
//        assertEquals(1, users.size());
//        assertEquals("Bob", users.get(0).getName());
//
//        users = userRepository.findByNameAndEmail("Alice", "alice@example.com");
//        assertEquals(1, users.size());
//        assertEquals("Alice", users.get(0).getName());
//    }
}
