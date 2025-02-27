package com.wallclubs.controllers;

import com.wallclubs.model.User;
import com.wallclubs.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("title", "Wallclubs - Sign Up");
        model.addAttribute("description", "Join the Wallclubs community!");
        model.addAttribute("canonical", "https://wallclubs.in/signup");
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email
    ) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setReferralCode(UUID.randomUUID().toString().substring(0, 8)); // Simple unique code
        userRepository.save(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("title", "Wallclubs - Login");
        model.addAttribute("description", "Log in to Wallclubs!");
        model.addAttribute("canonical", "https://wallclubs.in/login");
        return "login";
    }
}