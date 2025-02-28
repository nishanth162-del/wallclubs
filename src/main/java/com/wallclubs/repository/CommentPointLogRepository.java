package com.wallclubs.repository;

import com.wallclubs.model.CommentPointLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface CommentPointLogRepository extends JpaRepository<CommentPointLog, Long> {
    Optional<CommentPointLog> findByUsernameAndDate(String username, LocalDate date);
}