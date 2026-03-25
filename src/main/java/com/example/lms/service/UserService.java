package com.example.lms.service;

import com.example.lms.model.User;
import com.example.lms.model.enums.Role;
import com.example.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public long getTotalUsers() {
        return userRepository.count();
    }

    public void registerUser(String username, String password, String fullName, Role role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .role(role)
                .build();
        userRepository.save(user);
    }
}
