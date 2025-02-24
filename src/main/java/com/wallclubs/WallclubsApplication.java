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
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import com.wallclubs.model.User; // Add this import
import com.wallclubs.repository.UserRepository;// Add this import
import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
@Controller
public class WallclubsApplication {

    private final ArticleRepository articleRepository;
    private final PostRepository postRepository;
    private final ReferralRepository referralRepository;
    private final UserRepository userRepository;

    public WallclubsApplication(ArticleRepository articleRepository, PostRepository postRepository, ReferralRepository referralRepository,UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.postRepository = postRepository;
        this.referralRepository = referralRepository;
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(WallclubsApplication.class, args);
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
        String referralCode = username.toLowerCase() + (int)(Math.random() * 1000); // Simple unique code
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

    // Update this method in WallclubsApplication.java
    @GetMapping("/articles/{slug}")
    public String article(@PathVariable String slug, Model model) {
        List<Article> articles = articleRepository.findBySlug(slug);
        if (articles.isEmpty()) {
            System.out.println("No article found for slug: " + slug);
            return "404";
        }
        Article article = articles.get(0);
        model.addAttribute("title", article.getTitle() + " - Wallclubs");
        model.addAttribute("description", article.getContent().length() > 150 ? article.getContent().substring(0, 150) + "..." : article.getContent());
        model.addAttribute("article", article);
        model.addAttribute("canonical", "https://wallclubs.in/articles/" + slug);
        model.addAttribute("pageType", "article"); // Add this
        List<Article> allArticles = articleRepository.findAll();
        int currentIndex = allArticles.indexOf(article);
        Article nextArticle = (currentIndex + 1 < allArticles.size()) ? allArticles.get(currentIndex + 1) : null;
        model.addAttribute("nextArticle", nextArticle);
        return "article";
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Wallclubs - Smartphone Life Hacks");
        model.addAttribute("description", "Discover smartphone tips, join our community, and earn with affiliate clubs!");
        model.addAttribute("canonical", "https://wallclubs.in/");
        model.addAttribute("articles", articleRepository.findAll());
        model.addAttribute("pageType", "homepage");
        return "index"; // Must return "index"
    }
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title", "Wallclubs - About Us");
        model.addAttribute("description", "Learn about Wallclubs—your daily hub for smartphone hacks and tech news.");
        model.addAttribute("canonical", "https://wallclubs.in/about");
        model.addAttribute("pageType", "about"); // Add this
        return "about";
    }

    @GetMapping("/forum")
    public String forum(Model model) {
        model.addAttribute("title", "Wallclubs - Community Forum");
        model.addAttribute("description", "Join the discussion on smartphone tips and tricks!");
        model.addAttribute("posts", postRepository.findAll());
        model.addAttribute("canonical", "https://wallclubs.in/forum");
        return "forum";
    }
    @GetMapping("/category/{category}")
    public String category(@PathVariable String category, Model model) {
        List<Article> articles = articleRepository.findByCategory(category);
        model.addAttribute("title", "Wallclubs - " + category);
        model.addAttribute("description", "Explore the latest in " + category.toLowerCase() + "—tips, trends, and more!");
        model.addAttribute("canonical", "https://wallclubs.in/category/" + category);
        model.addAttribute("articles", articles);
        model.addAttribute("category", category);
        return "category";
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
            User user = userRepository.findByReferralCode(code); // Link if user exists
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
    @GetMapping("/track/{code}") // Temp for testing
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
        model.addAttribute("title", "Wallclubs - Affiliate Club");
        model.addAttribute("description", "Join our affiliate club and earn points by sharing!");
        model.addAttribute("canonical", "https://wallclubs.in/club");
        model.addAttribute("referrals", referralRepository.findAll());
        model.addAttribute("pageType", "club");
        // Add sample user for testing - replace with real user logic later
        User sampleUser = userRepository.findByUsername("nishanth"); // Test with your name
        if (sampleUser != null) {
            model.addAttribute("username", sampleUser.getUsername());
        }
        return "club";
    }



    @GetMapping("/submit")
    public String submitForm(Model model) {
        model.addAttribute("title", "Wallclubs - Submit Content");
        model.addAttribute("description", "Share your smartphone tips or forum posts!");
        model.addAttribute("canonical", "https://wallclubs.in/submit");

        return "submit";
    }

    @PostMapping("/submit/article")
    public String submitArticle(@RequestParam String title, @RequestParam String slug, @RequestParam String category, @RequestParam String content) {
        if (content.length() < 300) { // ~50-60 words, rough min
            return "redirect:/submit?error=content-too-short";
        }
        Article article = new Article();
        article.setTitle(title);
        article.setSlug(slug);
        article.setCategory(category);
        article.setContent(content);
        articleRepository.save(article);
        return "redirect:/articles/" + slug;
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
                referral.setPoints(referral.getPoints() + 10); // 10 points per qualified visit
                referralRepository.save(referral);
                return "Tracked";
            }
            return "Already tracked today";
        }
        return "Not found";
    }

    @PostMapping("/submit/post")
    public String submitPost(@RequestParam String title, @RequestParam String content, @RequestParam String author) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(author);
        postRepository.save(post);
        return "redirect:/forum";
    }
    @Bean
    CommandLineRunner init(ArticleRepository articleRepository, PostRepository postRepository, ReferralRepository referralRepository) {
        return args -> {
            // Smartphones
            if (articleRepository.findBySlug("best-smartphone-hacks").isEmpty()) {
                Article article = new Article();
                article.setTitle("10 Smartphone Hacks You’ll Wish You Knew Sooner");
                article.setSlug("best-smartphone-hacks");
                article.setCategory("Smartphones");
                article.setContent("Unlock your phone’s potential with these game-changing tips: 1. Use airplane mode to charge faster, 2. Customize your lock screen with widgets, 3. Enable dark mode to save battery, and more...");
                articleRepository.save(article);
            }
            if (articleRepository.findBySlug("battery-saving-tips").isEmpty()) {
                Article article = new Article();
                article.setTitle("Max Out Your Battery: 5 Proven Tricks");
                article.setSlug("battery-saving-tips");
                article.setCategory("Smartphones");
                article.setContent("Keep your phone alive all day: reduce brightness, turn off unused apps, use power-saving mode...");
                articleRepository.save(article);
            }
            if (articleRepository.findBySlug("fast-charging-guide").isEmpty()) {
                Article article = new Article();
                article.setTitle("Fast Charging 101: What You Need to Know");
                article.setSlug("fast-charging-guide");
                article.setCategory("Smartphones");
                article.setContent("Charge smarter: use the right cable, avoid overheating, and optimize settings...");
                articleRepository.save(article);
            }
            if (articleRepository.findBySlug("gadget-deals").isEmpty()) {
                Article article = new Article();
                article.setTitle("Best Gadget Deals This Month");
                article.setSlug("gadget-deals");
                article.setCategory("Smartphones");
                article.setContent("Score big savings on phones, earbuds, and chargers—here’s what’s on sale now...");
                articleRepository.save(article);
            }
            if (articleRepository.findBySlug("hidden-features-2025").isEmpty()) {
                Article article = new Article();
                article.setTitle("Hidden Smartphone Features Coming in 2025");
                article.setSlug("hidden-features-2025");
                article.setCategory("Smartphones");
                article.setContent("From secret gestures to AI tweaks—these features are coming soon...");
                articleRepository.save(article);
            }

            // Tech Tips
            if (articleRepository.findBySlug("tech-trends-2025").isEmpty()) {
                Article article = new Article();
                article.setTitle("Tech Trends Shaping 2025");
                article.setSlug("tech-trends-2025");
                article.setCategory("Tech Tips");
                article.setContent("From AI to foldables—here’s what’s next in tech this year...");
                articleRepository.save(article);
            }
            if (articleRepository.findBySlug("budget-tech-2025").isEmpty()) {
                Article article = new Article();
                article.setTitle("Best Budget Tech Gadgets for 2025");
                article.setSlug("budget-tech-2025");
                article.setCategory("Tech Tips");
                article.setContent("Affordable tech that punches above its weight—top picks for the year...");
                articleRepository.save(article);
            }
            if (articleRepository.findBySlug("privacy-hacks").isEmpty()) {
                Article article = new Article();
                article.setTitle("Privacy Hacks for Your Smartphone");
                article.setSlug("privacy-hacks");
                article.setCategory("Tech Tips");
                article.setContent("Keep your data safe: disable trackers, secure apps, and more...");
                articleRepository.save(article);
            }

            // Apps
            if (articleRepository.findBySlug("best-budget-apps").isEmpty()) {
                Article article = new Article();
                article.setTitle("Top Budget Apps to Save Money in 2025");
                article.setSlug("best-budget-apps");
                article.setCategory("Apps");
                article.setContent("Your wallet will thank you: budgeting tools, expense trackers, and more...");
                articleRepository.save(article);
            }
            if (articleRepository.findBySlug("app-security-tips").isEmpty()) {
                Article article = new Article();
                article.setTitle("Keep Your Apps Safe: Top Security Tips");
                article.setSlug("app-security-tips");
                article.setCategory("Apps");
                article.setContent("Protect your data with these must-know app security tricks...");
                articleRepository.save(article);
            }
            if (articleRepository.findBySlug("app-roundup-march").isEmpty()) {
                Article article = new Article();
                article.setTitle("March 2025 App Roundup: Must-Haves");
                article.setSlug("app-roundup-march");
                article.setCategory("Apps");
                article.setContent("The latest apps you can’t live without—productivity, fun, and more...");
                articleRepository.save(article);
            }
            if (articleRepository.findBySlug("app-privacy-2025").isEmpty()) {
                Article article = new Article();
                article.setTitle("App Privacy Updates You Need in 2025");
                article.setSlug("app-privacy-2025");
                article.setCategory("Apps");
                article.setContent("New laws and settings to protect your app data this year...");
                articleRepository.save(article);
            }

            // Current Affairs
            if (articleRepository.findBySlug("daily-news-update").isEmpty()) {
                Article article = new Article();
                article.setTitle("Tech News Roundup - Feb 23, 2025");
                article.setSlug("daily-news-update");
                article.setCategory("Current Affairs");
                article.setContent("Today’s headlines: new gadgets, app updates, and more breaking news...");
                articleRepository.save(article);
            }
            if (articleRepository.findBySlug("current-tech-scandals").isEmpty()) {
                Article article = new Article();
                article.setTitle("Tech Scandals Shaking Up 2025");
                article.setSlug("current-tech-scandals");
                article.setCategory("Current Affairs");
                article.setContent("From data breaches to lawsuits—here’s the buzz in tech today...");
                articleRepository.save(article);
            }

            // Lifestyle
            if (articleRepository.findBySlug("healthy-screen-time").isEmpty()) {
                Article article = new Article();
                article.setTitle("Master Your Screen Time: 7 Healthy Habits");
                article.setSlug("healthy-screen-time");
                article.setCategory("Lifestyle");
                article.setContent("Balance your digital life with these expert tips...");
                articleRepository.save(article);
            }
            if (articleRepository.findBySlug("weekend-reads").isEmpty()) {
                Article article = new Article();
                article.setTitle("Weekend Reads: Tech and Lifestyle Highlights");
                article.setSlug("weekend-reads");
                article.setCategory("Lifestyle");
                article.setContent("Unwind with these top stories and tips for your weekend...");
                articleRepository.save(article);
            }
            if (articleRepository.findBySlug("daily-life-hacks").isEmpty()) {
                Article article = new Article();
                article.setTitle("Daily Life Hacks Using Your Phone");
                article.setSlug("daily-life-hacks");
                article.setCategory("Lifestyle");
                article.setContent("Make every day easier with these smartphone tricks...");
                articleRepository.save(article);
            }
            // Forum and referral seeds unchanged
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
            if (articleRepository.findBySlug("screen-repair-hacks").isEmpty()) {
                Article article = new Article();
                article.setTitle("DIY Screen Repair Hacks for Your Phone");
                article.setSlug("screen-repair-hacks");
                article.setCategory("Smartphones");
                article.setContent("Cracked screen? Try these quick fixes before replacing it...");
                articleRepository.save(article);
            }
            if (articleRepository.findBySlug("productivity-apps").isEmpty()) {
                Article article = new Article();
                article.setTitle("Boost Productivity with These 5 Apps");
                article.setSlug("productivity-apps");
                article.setCategory("Apps");
                article.setContent("Get more done with these top-rated tools...");
                articleRepository.save(article);
            }
            if (articleRepository.findBySlug("tech-policy-2025").isEmpty()) {
                Article article = new Article();
                article.setTitle("Tech Policies to Watch in 2025");
                article.setSlug("tech-policy-2025");
                article.setCategory("Current Affairs");
                article.setContent("New regulations shaping the tech landscape...");
                articleRepository.save(article);
            }
        };
    }
}