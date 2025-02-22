package com.wallclubs;

import com.wallclubs.model.Article;
import com.wallclubs.model.Post;
import com.wallclubs.model.Referral;
import com.wallclubs.repository.ArticleRepository;
import com.wallclubs.repository.PostRepository;
import com.wallclubs.repository.ReferralRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@SpringBootApplication
@Controller
public class WallclubsApplication {

    private final ArticleRepository articleRepository;
    private final PostRepository postRepository;
    private final ReferralRepository referralRepository;

    public WallclubsApplication(ArticleRepository articleRepository, PostRepository postRepository, ReferralRepository referralRepository) {
        this.articleRepository = articleRepository;
        this.postRepository = postRepository;
        this.referralRepository = referralRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(WallclubsApplication.class, args);
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Wallclubs.in - Smartphone Life Hacks");
        model.addAttribute("description", "Discover smartphone tips, join our community, and earn with affiliate clubs!");
        model.addAttribute("canonical", "https://wallclubs.in/");
        return "index";
    }

    @GetMapping("/articles/{slug}")
    public String article(@PathVariable String slug, Model model) {
        List<Article> articles = articleRepository.findBySlug(slug);
        if (articles.isEmpty()) {
            System.out.println("No article found for slug: " + slug);
            return "404";
        }
        Article article = articles.get(0);
        model.addAttribute("title", article.getTitle() + " - Wallclubs.in");
        model.addAttribute("description", article.getContent().substring(0, Math.min(150, article.getContent().length())));
        model.addAttribute("article", article);
        model.addAttribute("canonical", "https://wallclubs.in/articles/" + slug);
        return "article";
    }

    @GetMapping("/forum")
    public String forum(Model model) {
        model.addAttribute("title", "Wallclubs.in - Community Forum");
        model.addAttribute("description", "Join the discussion on smartphone tips and tricks!");
        model.addAttribute("posts", postRepository.findAll());
        model.addAttribute("canonical", "https://wallclubs.in/forum");
        return "forum";
    }

    @GetMapping("/ref/{code}")
    public String referral(@PathVariable String code, Model model) {
        Referral referral = referralRepository.findByReferralCode(code);
        if (referral == null) {
            referral = new Referral();
            referral.setReferralCode(code);
            referral.setTotalVisits(0);
            referral.setQualifiedVisits(0);
            referralRepository.save(referral);
        }
        referral.setTotalVisits(referral.getTotalVisits() + 1);
        referralRepository.save(referral);
        model.addAttribute("title", "Wallclubs.in - Smartphone Life Hacks");
        model.addAttribute("description", "Discover smartphone tips, join our community, and earn with affiliate clubs!");
        model.addAttribute("canonical", "https://wallclubs.in/");
        model.addAttribute("referralCode", code); // Pass to JS for tracking
        return "index"; // Render home with tracking
    }

    @PostMapping("/track/{code}")
    @ResponseBody
    public String trackQualifiedVisit(@PathVariable String code) {
        Referral referral = referralRepository.findByReferralCode(code);
        if (referral != null) {
            referral.setQualifiedVisits(referral.getQualifiedVisits() + 1);
            referralRepository.save(referral);
            return "Tracked";
        }
        return "Not found";
    }

    @GetMapping("/club")
    public String club(Model model) {
        model.addAttribute("title", "Wallclubs.in - Affiliate Club");
        model.addAttribute("description", "Join our affiliate club and earn by sharing!");
        model.addAttribute("referrals", referralRepository.findAll());
        model.addAttribute("canonical", "https://wallclubs.in/club");
        return "club";
    }

    @Bean
    CommandLineRunner init(ArticleRepository articleRepository, PostRepository postRepository, ReferralRepository referralRepository) {
        return args -> {
            if (articleRepository.findBySlug("best-smartphone-hacks").isEmpty()) {
                Article article = new Article();
                article.setTitle("Best Smartphone Hacks for Beginners");
                article.setSlug("best-smartphone-hacks");
                article.setContent("Here are some amazing tips to get the most out of your smartphone...");
                articleRepository.save(article);
            }
            if (postRepository.findAll().isEmpty()) {
                Post post = new Post();
                post.setTitle("Favorite Phone Trick?");
                post.setContent("What’s your go-to smartphone hack?");
                post.setAuthor("Guest");
                postRepository.save(post);
            }
            if (referralRepository.findByReferralCode("test123") == null) {
                Referral referral = new Referral();
                referral.setReferralCode("test123");
                referral.setTotalVisits(0);
                referral.setQualifiedVisits(0);
                referralRepository.save(referral);
            }
        };
    }
}