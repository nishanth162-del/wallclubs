// src/main/java/com/wallclubs/repository/ReferralRepository.java
package com.wallclubs.repository;

import com.wallclubs.model.Referral;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReferralRepository extends JpaRepository<Referral, Long> {
    Referral findByReferralCode(String referralCode);
}