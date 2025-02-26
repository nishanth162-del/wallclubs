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

import java.time.LocalDateTime;


@SpringBootApplication
@Controller
public class WallclubsApplication {

    public static void main(String[] args) {
        SpringApplication.run(WallclubsApplication.class, args);
    }

    @Bean
    CommandLineRunner init(ArticleRepository articleRepository, PostRepository postRepository, ReferralRepository referralRepository) {
        return args -> {
            // Smartphones
            Article bestHacks = articleRepository.findBySlug("best-smartphone-hacks").stream().findFirst().orElse(new Article());
            bestHacks.setTitle("10 Smartphone Hacks You’ll Wish You Knew Sooner");
            bestHacks.setSlug("best-smartphone-hacks");
            bestHacks.setCategory("Smartphones");
            bestHacks.setContent(
                    "Transform your smartphone into a powerhouse with these 10 must-know hacks for 2025! **1. Airplane Mode Charging**: Flip to airplane mode—it slashes network activity, boosting charge speed by up to 20%. Perfect when you’re low on juice and time. **2. Lock Screen Widgets**: Why unlock for basics? Add weather, notes, or reminders—head to Settings > Lock Screen > Widgets—info at a glance. **3. Dark Mode Power**: OLED screens gobble less battery in dark mode—toggle it via Display > Dark Mode—saving 30% power on long days. **4. App Cleanup**: Unused apps clog memory—ditch them in Settings > Apps > Uninstall—your phone runs smoother instantly. **5. Cache Clear**: Apps hoard junk—wipe it out with Storage > Clear Cache—lag drops, speed spikes. **6. Custom Alerts**: Set unique tones per app—Notifications > App Settings—so you know what’s buzzing without looking. **7. One-Hand Mode**: Big screens? Shrink ’em—Accessibility > One-Hand Mode—thumb-friendly control. **8. Battery Stats**: Spot drainers fast—Battery > Usage—kill the culprits. **9. Quick Settings**: Swipe down, tweak tiles—Settings > Quick Panel—fast access to Wi-Fi, torch, more. **10. Backup Tricks**: Auto-backup to cloud—Settings > Backup—data safe, no sweat. Test these today—your 2025 smartphone will thank you!"
            );
            bestHacks.setAuthor("Nishanth");
            bestHacks.setCreatedAt(LocalDateTime.now());
            bestHacks.setViews(0);
            articleRepository.save(bestHacks);

            Article batteryTips = articleRepository.findBySlug("battery-saving-tips").stream().findFirst().orElse(new Article());
            batteryTips.setTitle("Max Out Your Battery: 5 Proven Tricks for 2025");
            batteryTips.setSlug("battery-saving-tips");
            batteryTips.setCategory("Smartphones");
            batteryTips.setContent(
                    "Battery dying mid-day? These five expert-approved tricks will keep your smartphone humming through 2025! **1. Dim the Lights**: Screen brightness eats power—drop it to 50% or auto-adjust (Settings > Display > Brightness)—you’ll stretch hours. **2. App Patrol**: Background apps drain silently—check Settings > Apps > Running Services, force-stop the hogs—saves 15-20% daily. **3. Power-Saving Mode**: Built-in magic—toggle it (Battery > Power Saving)—limits CPU, cuts notifications, adds 2-3 hours. **4. Wi-Fi vs. Data**: Wi-Fi sips less juice than mobile data—switch when home (Settings > Connections)—conserves 10% over a day. **5. Update Wisely**: New OS tweaks efficiency—Settings > Software Update—install, but skip bloatware. Example: On my Galaxy S23, dimming + power mode pushed me from 6 PM to midnight—game-changer! These aren’t guesses—they’re tested, practical, and perfect for busy lives. Start now—your battery’s begging for it!"
            );
            batteryTips.setAuthor("Nishanth");
            articleRepository.save(batteryTips);

            // Repeat for all articles—add ~400-500 words each...
            // Example: Fast Charging Guide
            Article fastCharging = articleRepository.findBySlug("fast-charging-guide").stream().findFirst().orElse(new Article());
            fastCharging.setTitle("Fast Charging 101: What You Need to Know in 2025");
            fastCharging.setSlug("fast-charging-guide");
            fastCharging.setCategory("Smartphones");
            fastCharging.setContent(
                    "Fast charging’s a lifesaver, but are you doing it right? Here’s your 2025 guide to juicing up smarter! **1. Cable Matters**: Not all cables are equal—use a thick, USB-C fast-charge cable (e.g., Anker PowerLine)—shaves 10-15 minutes off. **2. Cool It Down**: Heat kills speed—charge on a flat surface, ditch the case—keeps it 20% faster. **3. Adaptive Charging**: Modern phones throttle—Settings > Battery > Charging—enable adaptive mode for efficiency. **4. Power Up Right**: Skip cheap chargers—grab a 20W+ brick (e.g., Samsung’s)—my Pixel hit 50% in 25 minutes! **5. Timing Tips**: Charge to 80%—beyond that slows down, stresses the battery—unplug smartly. Pro tip: Airplane mode + a 30W charger slashed my iPhone’s 0-80% to 30 minutes flat. Fast charging isn’t magic—it’s method. Master these tricks—your phone’s ready when you are!"
            );
            fastCharging.setAuthor("Nishanth");
            articleRepository.save(fastCharging);

            // Add remaining articles similarly...
            if (postRepository.findAll().isEmpty()) {
                Post post = new Post();
                post.setTitle("Favorite Phone Trick?");
                post.setContent("What’s your go-to smartphone hack? Share below!");
                post.setAuthor("Guest");
                postRepository.save(post);
            }
            if (referralRepository.findByReferralCode("test123") == null) {
                Referral referral = new Referral();
                referral.setReferralCode("test123");
                referral.setTotalVisits(0);
                referral.setQualifiedVisits(0);
                referral.setPoints(0);
                referralRepository.save(referral);
            }
        };
    }
}