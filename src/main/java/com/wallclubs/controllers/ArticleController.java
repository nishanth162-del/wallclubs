package com.wallclubs.controllers;

import com.wallclubs.model.Article;
import com.wallclubs.model.Comment;
import com.wallclubs.repository.ArticleRepository;
import com.wallclubs.repository.CommentRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ArticleController {
    private final ArticleRepository articleRepository;
    private final CommentRepository commentRepository;

    public ArticleController(ArticleRepository articleRepository, CommentRepository commentRepository) {
        this.articleRepository = articleRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/articles/{slug}")
    public String article(@PathVariable String slug, Model model) {
        List<Article> articles = articleRepository.findBySlug(slug);
        if (articles.isEmpty()) return "404";
        Article article = articles.get(0);
        article.setViews(article.getViews() + 1);
        articleRepository.save(article);

        model.addAttribute("title", article.getTitle() + " - Wallclubs");
        model.addAttribute("description", article.getDescription()); // Use stored description
        model.addAttribute("article", article);
        model.addAttribute("canonical", "https://wallclubs.in/articles/" + slug);
        model.addAttribute("pageType", "article");
        List<Article> allArticles = articleRepository.findAll();
        int currentIndex = allArticles.indexOf(article);
        Article nextArticle = (currentIndex + 1 < allArticles.size()) ? allArticles.get(currentIndex + 1) : null;
        model.addAttribute("nextArticle", nextArticle);
        model.addAttribute("comments", commentRepository.findByArticleId(article.getId()));
        return "article";
    }

    @PostMapping("/articles/{slug}/comment")
    public String addComment(@PathVariable String slug, @RequestParam String author, @RequestParam String text) {
        List<Article> articles = articleRepository.findBySlug(slug);
        if (articles.isEmpty()) return "404";
        Article article = articles.get(0);
        Comment comment = new Comment();
        comment.setArticleId(article.getId());
        comment.setAuthor(author);
        comment.setText(text);
        commentRepository.save(comment);
        return "redirect:/articles/" + slug;
    }



    @GetMapping("/")
    public String home(Model model) {
        List<Article> articles = articleRepository.findAll();
        model.addAttribute("title", "Wallclubs - Smartphone Life Hacks");
        model.addAttribute("description", "Discover smartphone tips, join our community, and earn with affiliate clubs!");
        model.addAttribute("canonical", "https://wallclubs.in/");
        model.addAttribute("articles", articles);
        model.addAttribute("categories", articles.stream()
                .map(Article::getCategory)
                .distinct()
                .sorted()
                .toList());
        model.addAttribute("pageType", "homepage");
        return "index";
    }



    @GetMapping("/articles")
    public String articlesByCategory(@RequestParam(required = false) String category, Model model) {
        List<Article> articles = category != null ? articleRepository.findByCategory(category) : articleRepository.findAll();
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
        model.addAttribute("pageType", "articles");
        return "articles";
    }

    @GetMapping("/submit")
    public String submitForm(Model model) {
        model.addAttribute("title", "Wallclubs - Submit Content");
        model.addAttribute("description", "Share your smartphone tips!");
        model.addAttribute("canonical", "https://wallclubs.in/submit");
        model.addAttribute("categories", articleRepository.findAll().stream()
                .map(Article::getCategory)
                .distinct()
                .sorted()
                .toList());
        return "submit";
    }

    @PostMapping("/submit/article")
    public String submitArticle(
            @RequestParam String title,
            @RequestParam String slug,
            @RequestParam String category,
            @RequestParam String description, // New field
            @RequestParam String content
    ) {
        if (content.length() < 50) return "redirect:/submit?error=content-too-short";
        if (description.length() > 150) return "redirect:/submit?error=description-too-long";
        Article article = new Article();
        article.setTitle(title);
        article.setSlug(slug);
        article.setCategory(category);
        article.setDescription(description); // Store plain text directly
        article.setContent(content);
        article.setAuthor("Nishanth");
        article.setCreatedAt(LocalDateTime.now());
        article.setViews(0);
        articleRepository.save(article);
        return "redirect:/articles/" + slug;
    }
}