package com.example.HabitTracker.services;

import com.example.HabitTracker.dto.UserDTO;
import com.example.HabitTracker.dto.LoginDTO;
import com.example.HabitTracker.models.User;
import com.example.HabitTracker.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Create a new user
    public UserDTO createUser(UserDTO userDTO) {
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());

        // Hash the password before saving
        String hashedPassword = passwordEncoder.encode(userDTO.getPasswordHash());
        user.setPasswordHash(hashedPassword);

        user.setAvatar(userDTO.getAvatar());

        User savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

    public UserDTO login(LoginDTO loginDTO) {
        Optional<User> userOptional = userRepository.findByEmail(loginDTO.getEmail());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Invalid email or password");
        }

        User user = userOptional.get();

        // Validate password (hashed)
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        return mapToDTO(user);
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
        return userDTO;
    }
}