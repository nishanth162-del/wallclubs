package com.wallclubs.controllers;

import com.wallclubs.model.Referral;
import com.wallclubs.model.User;
import com.wallclubs.repository.ReferralRepository;
import com.wallclubs.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

@Controller
public class UserController {
    private final ReferralRepository referralRepository;
    private final UserRepository userRepository;

    public UserController(ReferralRepository referralRepository, UserRepository userRepository) {
        this.referralRepository = referralRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("title", "Wallclubs - Sign Up");
        model.addAttribute("description", "Join Wallclubs to share and earn!");
        model.addAttribute("canonical", "https://wallclubs.in/signup");
        model.addAttribute("pageType", "signup");
        return "signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@RequestParam String username, @RequestParam String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        String referralCode = username.toLowerCase() + (int)(Math.random() * 1000);
        user.setReferralCode(referralCode);
        userRepository.save(user);

        Referral referral = new Referral();
        referral.setReferralCode(referralCode);
        referral.setTotalVisits(0);
        referral.setQualifiedVisits(0);
        referral.setPoints(0);
        referral.setUser(user);
        referralRepository.save(referral);
        return "redirect:/club";
    }

    @GetMapping("/club")
    public String club(Model model) {
        model.addAttribute("title", "Wallclubs - Affiliate Club");
        model.addAttribute("description", "Join our affiliate club and earn points by sharing!");
        model.addAttribute("canonical", "https://wallclubs.in/club");
        model.addAttribute("referrals", referralRepository.findAll());
        model.addAttribute("pageType", "club");
        User sampleUser = userRepository.findByUsername("nishanth");
        model.addAttribute("username", sampleUser != null ? sampleUser.getUsername() : "Guest");
        return "club";
    }

    @GetMapping("/ref/{code}")
    public String referral(@PathVariable String code, Model model, HttpServletRequest request) {
        Referral referral = referralRepository.findByReferralCode(code);
        if (referral == null) {
            referral = new Referral();
            referral.setReferralCode(code);
            referral.setTotalVisits(0);
            referral.setQualifiedVisits(0);
            referral.setPoints(0);
            User user = userRepository.findByReferralCode(code);
            if (user != null) referral.setUser(user);
            referralRepository.save(referral);
        }
        referral.setTotalVisits(referral.getTotalVisits() + 1);
        referralRepository.save(referral);
        model.addAttribute("title", "Wallclubs - Smartphone Life Hacks");
        model.addAttribute("description", "Discover smartphone tips, join our community, and earn with affiliate clubs!");
        model.addAttribute("canonical", "https://wallclubs.in/");
        model.addAttribute("referralCode", code);
        model.addAttribute("pageType", "homepage");
        return "index";
    }

    @PostMapping("/track/{code}")
    @ResponseBody
    public String trackQualifiedVisit(@PathVariable String code, HttpServletRequest request) {
        Referral referral = referralRepository.findByReferralCode(code);
        if (referral != null) {
            String ip = request.getRemoteAddr();
            LocalDateTime now = LocalDateTime.now();
            if (!ip.equals(referral.getLastVisitIp()) || referral.getLastVisitTime() == null || referral.getLastVisitTime().isBefore(now.minusDays(1))) {
                referral.setQualifiedVisits(referral.getQualifiedVisits() + 1);
                referral.setLastVisitIp(ip);
                referral.setLastVisitTime(now);
                referral.setPoints(referral.getPoints() + 10);
                referralRepository.save(referral);
                return "Tracked";
            }
            return "Already tracked today";
        }
        return "Not found";
    }
}