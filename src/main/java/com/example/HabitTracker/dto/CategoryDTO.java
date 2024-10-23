package com.example.HabitTracker.dto;

import com.example.HabitTracker.models.Category;

public class CategoryDTO {

    private Long id;
    private String name;

    // Constructor
    public CategoryDTO() {}

    public CategoryDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}