// src/main/java/com/wallclubs/repository/PostRepository.java
package com.wallclubs.repository;

import com.wallclubs.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}