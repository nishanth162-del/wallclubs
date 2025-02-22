// src/main/java/com/wallclubs/model/Referral.java
package com.wallclubs.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Referral {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String referralCode; // e.g., "user123"
    private int totalVisits;
    private int qualifiedVisits; // Visits ≥ 5 mins
}