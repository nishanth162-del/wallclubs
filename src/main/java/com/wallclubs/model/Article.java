// src/main/java/com/wallclubs/model/Article.java
package com.wallclubs.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String slug;
    // SEO-friendly URL part (e.g., "best-smartphone-hacks")
    @Column(length = 2000)
    private String content;
    private String category;

}