package com.kai.test_practice.repositories;


import com.kai.test_practice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE (:name IS NULL OR u.name = :name) AND (:email IS NULL OR u.email = :email)")
    List<User> findByNameAndEmail(@Param("name") String name, @Param("email") String email);

}
