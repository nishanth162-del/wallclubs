package com.wallclubs.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Referral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String referralCode;
    private int totalVisits;
    private int qualifiedVisits;
    private String lastVisitIp;       // Track IP
    private LocalDateTime lastVisitTime; // Track time
    private int points;

    @ManyToOne
    private User user;// Points instead of cash
}