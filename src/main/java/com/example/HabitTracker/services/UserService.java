package com.example.HabitTracker.services;

import com.example.HabitHub.dto.UserDTO;
import com.example.HabitTracker.models.User;
import com.example.HabitTracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Create a new user
    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        user.setAvatar(userDTO.getAvatar());
        user.setPasswordHash(userDTO.getPasswordHash()); // Set the password hash

        User savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

    // Get a user by email
    public UserDTO getUserByEmail(String email) {
        Optional<User> user = userRepository.findById(email);
        return user.map(this::mapToDTO).orElse(null);
    }

    // Update a user by email
    public UserDTO updateUser(String email, UserDTO userDTO) {
        Optional<User> existingUser = userRepository.findById(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(userDTO.getName());
            user.setAvatar(userDTO.getAvatar());
            if (userDTO.getPasswordHash() != null) {
                user.setPasswordHash(userDTO.getPasswordHash()); // Update password if provided
            }
            User updatedUser = userRepository.save(user);
            return mapToDTO(updatedUser);
        }

        return null;
    }

    // Delete a user by email
    public void deleteUser(String email) {
        userRepository.deleteById(email);
    }

    // Helper method to map User to UserDTO
    private UserDTO mapToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setName(user.getName());
        userDTO.setAvatar(user.getAvatar());
        userDTO.setPasswordHash(user.getPasswordHash());
        return userDTO;
    }
}