package com.wallclubs.controllers;

import com.wallclubs.model.Article;
import com.wallclubs.model.Comment;
import com.wallclubs.model.CommentPointLog;
import com.wallclubs.model.User;
import com.wallclubs.repository.ArticleRepository;
import com.wallclubs.repository.CommentRepository;
import com.wallclubs.repository.CommentPointLogRepository;
import com.wallclubs.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ArticleController {
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;
    private final CommentPointLogRepository commentPointLogRepository;
    private final UserRepository userRepository;

    public ArticleController(ArticleRepository articleRepository, CommentRepository commentRepository,
                             CommentPointLogRepository commentPointLogRepository, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
        this.commentPointLogRepository = commentPointLogRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/articles/{slug}")
    public String article(@PathVariable String slug, Model model) {
        List<Article> articles = articleRepository.findBySlug(slug);
        if (articles.isEmpty()) return "404";
        Article article = articles.get(0);

        boolean isAdmin = false;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        }

        if (!isAdmin && (!"approved".equals(article.getStatus()) && article.getStatus() != null)) {
            return "404";
        }

        article.setViews(article.getViews() + 1);
        articleRepository.save(article);

        List<Article> allArticles = articleRepository.findAll().stream()
                .filter(a -> "approved".equals(a.getStatus()) || a.getStatus() == null)
                .collect(Collectors.toList());
        List<Article> trendingArticles = allArticles.stream()
                .sorted((a1, a2) -> Integer.compare(a2.getViews(), a1.getViews()))
                .limit(3)
                .toList();

        model.addAttribute("title", article.getTitle() + " - Wallclubs");
        model.addAttribute("description", article.getDescription());
        model.addAttribute("article", article);
        model.addAttribute("canonical", "https://wallclubs.in/articles/" + slug);
        model.addAttribute("pageType", "article");
        int currentIndex = allArticles.indexOf(article);
        Article nextArticle = (currentIndex + 1 < allArticles.size()) ? allArticles.get(currentIndex + 1) : null;
        model.addAttribute("nextArticle", nextArticle);
        model.addAttribute("comments", commentRepository.findByArticleId(article.getId()));
        model.addAttribute("categories", allArticles.stream()
                .map(Article::getCategory)
                .distinct()
                .sorted()
                .toList());
        model.addAttribute("trendingArticles", trendingArticles);
        model.addAttribute("selectedCategory", article.getCategory());
        return "article";
    }

    @PostMapping("/articles/{slug}/comment")
    public String addComment(@PathVariable String slug, @RequestParam String text) {
        List<Article> articles = articleRepository.findBySlug(slug);
        if (articles.isEmpty() || (!"approved".equals(articles.get(0).getStatus()) && articles.get(0).getStatus() != null)) return "404";
        Article article = articles.get(0);

        // Get author from authenticated user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String author = principal instanceof UserDetails ? ((UserDetails) principal).getUsername() : "Anonymous";

        // Save comment regardless of points
        Comment comment = new Comment();
        comment.setArticleId(article.getId());
        comment.setAuthor(author);
        comment.setText(text);
        commentRepository.save(comment);

        // Award 2 points per comment, cap at 10/day
        User user = userRepository.findByUsername(author);
        if (user != null) {
            LocalDate today = LocalDate.now();
            CommentPointLog log = commentPointLogRepository.findByUsernameAndDate(author, today)
                    .orElse(new CommentPointLog());
            if (log.getId() == null) {
                log.setUsername(author);
                log.setDate(today);
                log.setPointsEarned(0);
            }
            if (log.getPointsEarned() < 10) {
                int pointsToAdd = Math.min(2, 10 - log.getPointsEarned()); // Max 10/day
                user.setPoints(user.getPoints() + pointsToAdd);
                log.setPointsEarned(log.getPointsEarned() + pointsToAdd);
                userRepository.save(user);
                commentPointLogRepository.save(log);
            }
        }

        return "redirect:/articles/" + slug;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Article> articles = articleRepository.findAll().stream()
                .filter(a -> "approved".equals(a.getStatus()) || a.getStatus() == null)
                .collect(Collectors.toList());
        List<Article> trendingArticles = articles.stream()
                .sorted((a1, a2) -> Integer.compare(a2.getViews(), a1.getViews()))
                .limit(3)
                .toList();

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            logger.info("User logged in: {}", username);
        } else {
            logger.info("No user logged in, principal: {}", principal);
        }

        model.addAttribute("title", "Wallclubs - Smartphone Life Hacks");
        model.addAttribute("description", "Discover smartphone tips, join our community, and earn with affiliate clubs!");
        model.addAttribute("canonical", "https://wallclubs.in/");
        model.addAttribute("articles", articles);
        model.addAttribute("categories", articles.stream()
                .map(Article::getCategory)
                .distinct()
                .sorted()
                .toList());
        model.addAttribute("trendingArticles", trendingArticles);
        model.addAttribute("pageType", "homepage");
        return "index";
    }

    @GetMapping("/articles")
    public String articlesByCategory(@RequestParam(required = false) String category, Model model) {
        List<Article> articles = category != null ?
                articleRepository.findByCategory(category).stream()
                        .filter(a -> "approved".equals(a.getStatus()) || a.getStatus() == null)
                        .collect(Collectors.toList()) :
                articleRepository.findAll().stream()
                        .filter(a -> "approved".equals(a.getStatus()) || a.getStatus() == null)
                        .collect(Collectors.toList());
        List<Article> trendingArticles = articles.stream()
                .sorted((a1, a2) -> Integer.compare(a2.getViews(), a1.getViews()))
                .limit(3)
                .toList();
        String pageDescription = articles.isEmpty() ? "No articles available." : articles.get(0).getDescription();

        model.addAttribute("title", category != null ? "Wallclubs - " + category : "Wallclubs - All Articles");
        model.addAttribute("description", pageDescription);
        model.addAttribute("canonical", category != null ? "https://wallclubs.in/articles?category=" + category : "https://wallclubs.in/articles");
        model.addAttribute("articles", articles);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("categories", articles.stream()
                .map(Article::getCategory)
                .distinct()
                .sorted()
                .toList());
        model.addAttribute("trendingArticles", trendingArticles);
        model.addAttribute("pageType", "articles");
        return "articles";
    }

    @GetMapping("/submit")
    public String submitForm(Model model) {
        List<Article> articles = articleRepository.findAll().stream()
                .filter(a -> "approved".equals(a.getStatus()) || a.getStatus() == null)
                .collect(Collectors.toList());
        List<Article> trendingArticles = articles.stream()
                .sorted((a1, a2) -> Integer.compare(a2.getViews(), a1.getViews()))
                .limit(3)
                .toList();
        model.addAttribute("title", "Wallclubs - Submit Content");
        model.addAttribute("description", "Share your smartphone tips!");
        model.addAttribute("canonical", "https://wallclubs.in/submit");
        model.addAttribute("categories", articles.stream()
                .map(Article::getCategory)
                .distinct()
                .sorted()
                .toList());
        model.addAttribute("trendingArticles", trendingArticles);
        return "submit";
    }

    @PostMapping("/submit/article")
    public String submitArticle(
            @RequestParam String title,
            @RequestParam String slug,
            @RequestParam String category,
            @RequestParam String description,
            @RequestParam String content
    ) {
        if (content.length() < 50) return "redirect:/submit?error=content-too-short";
        if (description.length() > 150) return "redirect:/submit?error=description-too-long";
        Article article = new Article();
        article.setTitle(title);
        article.setSlug(slug);
        article.setCategory(category);
        article.setDescription(description);
        article.setContent(content);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal instanceof UserDetails ? ((UserDetails) principal).getUsername() : "Anonymous";
        article.setAuthor(username);
        article.setCreatedAt(LocalDateTime.now());
        article.setViews(0);
        article.setStatus("pending");
        articleRepository.save(article);
        return "redirect:/submit?success=submitted-awaiting-approval";
    }
}