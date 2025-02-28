package com.wallclubs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.titan.email");
        mailSender.setPort(465);
        mailSender.setUsername("contact@wallclubs.in");
        mailSender.setPassword("9):v>GUL4^4b?v=");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true"); // SSL instead of TLS
        props.put("mail.debug", "true"); // Debug logs
        return mailSender;
    }
}


