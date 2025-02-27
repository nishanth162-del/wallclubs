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


}