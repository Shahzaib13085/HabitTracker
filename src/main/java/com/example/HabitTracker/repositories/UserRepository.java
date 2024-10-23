package com.example.HabitTracker.repositories;

import com.example.HabitTracker.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email); // Custom query method
}