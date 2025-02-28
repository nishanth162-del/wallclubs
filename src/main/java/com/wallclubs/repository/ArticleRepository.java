// src/main/java/com/wallclubs/repository/ArticleRepository.java
package com.wallclubs.repository;

import com.wallclubs.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findBySlug(String slug);
    List<Article> findByCategory(String category);
    List<Article> findByStatus(String status); // New
    List<Article> findByCategoryAndStatus(String category, String status);
}