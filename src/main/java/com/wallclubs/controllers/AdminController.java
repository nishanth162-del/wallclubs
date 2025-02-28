package com.wallclubs.controllers;

import com.wallclubs.model.Article;
import com.wallclubs.model.User;
import com.wallclubs.repository.ArticleRepository;
import com.wallclubs.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    public AdminController(ArticleRepository articleRepository, UserRepository userRepository, JavaMailSender mailSender) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @GetMapping("/admin/articles")
    public String adminArticles(Model model) {
        List<Article> pendingArticles = articleRepository.findByStatus("pending");
        model.addAttribute("pendingArticles", pendingArticles);
        return "admin/articles";
    }

    @PostMapping("/admin/approve")
    public String approveArticle(@RequestParam Long articleId) {
        Article article = articleRepository.findById(articleId).orElseThrow();
        article.setStatus("approved");
        articleRepository.save(article);

        User user = userRepository.findByUsername(article.getAuthor());
        if (user != null) {
            int currentPoints = user.getPoints() != null ? user.getPoints() : 0; // Handle null
            user.setPoints(currentPoints + 10);
            userRepository.save(user);
        }

        return "redirect:/admin/articles";
    }

    @PostMapping("/admin/reject")
    public String rejectArticle(
            @RequestParam Long articleId,
            @RequestParam String rejectionReason
    ) {
        Article article = articleRepository.findById(articleId).orElseThrow();
        article.setStatus("rejected");
        article.setRejectionReason(rejectionReason);
        articleRepository.save(article);

        // Email still disabled—add later
        /*
        User user = userRepository.findByUsername(article.getAuthor());
        if (user != null && user.getEmail() != null) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Wallclubs Article Submission Rejected");
            message.setText("Your article '" + article.getTitle() + "' was rejected for: " + rejectionReason +
                            "\n\nReview our Terms & Conditions at https://wallclubs.in/terms.");
            mailSender.send(message);
        }
        */

        return "redirect:/admin/articles";
    }
}