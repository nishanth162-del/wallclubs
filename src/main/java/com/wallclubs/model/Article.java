package com.wallclubs.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;

@Entity
@Getter
@Setter
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String slug;
    @Column(columnDefinition = "TEXT")
    private String content;
    private String category;
    private String author;
    @Column(columnDefinition = "TEXT")
    private String description; // Add this field
    private java.time.LocalDateTime createdAt;
    private int views;
    private String status = "pending"; // "pending", "approved", "rejected"
    @Column(columnDefinition = "TEXT")
    private String rejectionReason;
}