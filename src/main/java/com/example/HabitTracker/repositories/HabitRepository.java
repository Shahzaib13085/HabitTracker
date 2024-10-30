package com.example.HabitTracker.repositories;

import com.example.HabitTracker.models.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByUserEmail(String email);
    List<Habit> findByCategoryId(Long categoryId);
    List<Habit> findByEndDateAndCompleted(LocalDate endDate, Boolean completed);
}