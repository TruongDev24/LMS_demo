package com.example.lms.controller;

import com.example.lms.model.enums.Role;
import com.example.lms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String fullName,
                             @RequestParam String role,
                             RedirectAttributes redirectAttributes) {
        try {
            Role userRole = Role.valueOf(role.toUpperCase());
            userService.registerUser(username, password, fullName, userRole);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful. Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("username", username);
            redirectAttributes.addFlashAttribute("fullName", fullName);
            redirectAttributes.addFlashAttribute("role", role);
            return "redirect:/register";
        }
    }
}
