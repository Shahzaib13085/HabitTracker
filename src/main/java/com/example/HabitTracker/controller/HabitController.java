package com.example.HabitTracker.controller;

import com.example.HabitTracker.dto.HabitDTO;
import com.example.HabitTracker.services.HabitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/habits")
public class HabitController {

    @Autowired
    private HabitService habitService;

    @PostMapping
    public ResponseEntity<HabitDTO> createHabit(@RequestBody HabitDTO habitDTO) {
        HabitDTO createdHabit = habitService.createHabit(habitDTO);
        return ResponseEntity.ok(createdHabit);
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<List<HabitDTO>> getHabitsForUser(@PathVariable String email) {
        List<HabitDTO> habits = habitService.getHabitsForUser(email);
        return ResponseEntity.ok(habits);
    }

    @PutMapping("/{habitId}/complete")
    public ResponseEntity<HabitDTO> completeHabit(@PathVariable Long habitId) {
        HabitDTO habitDTO = habitService.completeHabit(habitId);
        return ResponseEntity.ok(habitDTO);
    }

    @DeleteMapping("/{habitId}")
    public ResponseEntity<String> deleteHabit(@PathVariable Long habitId) {
        habitService.deleteHabit(habitId);
        return ResponseEntity.ok("Habit deleted successfully.");
    }

    @PutMapping("/{habitId}")
    public ResponseEntity<HabitDTO> updateHabit(@PathVariable Long habitId, @RequestBody HabitDTO habitDTO) {
        HabitDTO updatedHabit = habitService.updateHabit(habitId, habitDTO);
        return ResponseEntity.ok(updatedHabit);
    }
}