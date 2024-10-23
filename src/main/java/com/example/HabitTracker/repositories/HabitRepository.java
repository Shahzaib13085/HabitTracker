package com.example.HabitTracker.repositories;

import com.example.HabitTracker.models.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByUserEmail(String email);
}