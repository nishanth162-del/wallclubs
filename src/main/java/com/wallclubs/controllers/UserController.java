package com.wallclubs.controllers;

import com.wallclubs.model.User;
import com.wallclubs.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        user.setReferralCode(UUID.randomUUID().toString().substring(0, 8));
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

    @GetMapping("/profile")
    public String profile(Model model) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal instanceof UserDetails ? ((UserDetails) principal).getUsername() : "Anonymous";
        User user = userRepository.findByUsername(username);
        if (user == null) return "redirect:/login";

        // For now, successful referrals are placeholder—implement later with referral tracking
        long successfulReferrals = 0; // Placeholder—add logic later

        model.addAttribute("title", "Wallclubs - Profile");
        model.addAttribute("description", "Manage your Wallclubs profile!");
        model.addAttribute("canonical", "https://wallclubs.in/profile");
        model.addAttribute("user", user);
        model.addAttribute("successfulReferrals", successfulReferrals);
        return "profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword
    ) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal instanceof UserDetails ? ((UserDetails) principal).getUsername() : "Anonymous";
        User user = userRepository.findByUsername(username);
        if (user == null) return "redirect:/login";

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return "redirect:/profile?error=wrong-password";
        }
        if (!newPassword.equals(confirmPassword)) {
            return "redirect:/profile?error=password-mismatch";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return "redirect:/profile?success=password-changed";
    }
}