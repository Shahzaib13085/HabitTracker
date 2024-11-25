package com.example.HabitTracker.services;

import com.example.HabitTracker.dto.HabitDTO;
import com.example.HabitTracker.models.Category;
import com.example.HabitTracker.models.Habit;
import com.example.HabitTracker.models.User;
import com.example.HabitTracker.repositories.CategoryRepository;
import com.example.HabitTracker.repositories.HabitRepository;
import com.example.HabitTracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HabitService {

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // Create a new habit
    public HabitDTO createHabit(HabitDTO habitDTO) {
        Habit habit = new Habit();

        // Fetch the user by email
        Optional<User> userOptional = userRepository.findByEmail(habitDTO.getUserEmail());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with email: " + habitDTO.getUserEmail());
        }
        User user = userOptional.get();

        // Fetch the category by ID
        Optional<Category> categoryOptional = categoryRepository.findById(habitDTO.getCategoryId());
        if (categoryOptional.isEmpty()) {
            throw new RuntimeException("Category not found with ID: " + habitDTO.getCategoryId());
        }
        Category category = categoryOptional.get();

        // Set habit fields
        habit.setName(habitDTO.getName());
        habit.setDescription(habitDTO.getDescription());
        habit.setFrequency(habitDTO.getFrequency());
        habit.setDifficulty(habitDTO.getDifficulty());
        habit.setStartDate(habitDTO.getStartDate());
        habit.setEndDate(habitDTO.getEndDate());

        // Default status to 'active' if not provided
        habit.setStatus(habitDTO.getStatus() != null ? habitDTO.getStatus() : "active");

        // Set the user and category
        habit.setUser(user);
        habit.setCategory(category);

        // Save the habit to the database
        Habit savedHabit = habitRepository.save(habit);

        return convertToDTO(savedHabit);
    }

    // Get all habits for a user
    public List<HabitDTO> getHabitsForUser(String userEmail) {
        List<Habit> habits = habitRepository.findByUserEmail(userEmail);
        return habits.stream().map(this::convertToDTO).toList();
    }

    // Update an existing habit
    public HabitDTO updateHabit(Long habitId, HabitDTO habitDTO) {
        Optional<Habit> habitOptional = habitRepository.findById(habitId);
        if (habitOptional.isEmpty()) {
            throw new RuntimeException("Habit not found with ID: " + habitId);
        }

        Habit habit = habitOptional.get();

        habit.setName(habitDTO.getName());
        habit.setDescription(habitDTO.getDescription());
        habit.setFrequency(habitDTO.getFrequency());
        habit.setDifficulty(habitDTO.getDifficulty());
        habit.setStartDate(habitDTO.getStartDate());
        habit.setEndDate(habitDTO.getEndDate());

        if (habitDTO.getStatus() != null) {
            habit.setStatus(habitDTO.getStatus());
        }

        // Set new category if updated
        if (habitDTO.getCategoryId() != null) {
            Optional<Category> category = categoryRepository.findById(habitDTO.getCategoryId());
            if (category.isPresent()) {
                habit.setCategory(category.get());
            }
        }

        Habit updatedHabit = habitRepository.save(habit);

        return convertToDTO(updatedHabit);
    }

    // Delete a habit by ID
    public void deleteHabit(Long habitId) {
        habitRepository.deleteById(habitId);
    }

    // Convert Habit to HabitDTO
    private HabitDTO convertToDTO(Habit habit) {
        HabitDTO habitDTO = new HabitDTO();
        habitDTO.setId(habit.getId());
        habitDTO.setName(habit.getName());
        habitDTO.setDescription(habit.getDescription());
        habitDTO.setFrequency(habit.getFrequency());
        habitDTO.setDifficulty(habit.getDifficulty());
        habitDTO.setStartDate(habit.getStartDate());
        habitDTO.setEndDate(habit.getEndDate());
        habitDTO.setStatus(habit.getStatus());
        habitDTO.setUserEmail(habit.getUser().getEmail());
        habitDTO.setCategoryId(habit.getCategory().getId());
        habitDTO.setCompleted(habit.getCompleted());
        return habitDTO;
    }

    public HabitDTO completeHabit(Long habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        habit.setCompleted(true);
        Habit updatedHabit = habitRepository.save(habit);
        return convertToDTO(updatedHabit);
    }

    @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
    public void notifyUpcomingEndDate() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        // Find habits that are due tomorrow and are not completed
        List<Habit> habitsDueTomorrow = habitRepository.findByEndDateAndCompleted(tomorrow, false);

        for (Habit habit : habitsDueTomorrow) {
            notificationService.sendNotification(
                    habit.getUser().getEmail(),
                    "Reminder: Habit Due Tomorrow",
                    "Your habit \"" + habit.getName() + "\" is due tomorrow. Please complete it to stay on track!"
            );
        }
    }
}