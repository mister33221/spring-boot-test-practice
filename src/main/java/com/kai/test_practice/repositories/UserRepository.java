package com.kai.test_practice.repositories;


import com.kai.test_practice.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(@NotNull @NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email);
}
