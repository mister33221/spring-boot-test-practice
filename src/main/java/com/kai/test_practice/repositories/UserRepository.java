package com.kai.test_practice.repositories;


import com.kai.test_practice.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE (:name IS NULL OR u.name = :name) AND (:email IS NULL OR u.email = :email)")
    List<User> findByNameAndEmail(@Param("name") String name, @Param("email") String email);

    boolean existsByEmail(@NotNull @NotBlank(message = "Email is required") @Email(message = "Email should be valid") String email);
}
