package com.medflow.medflowauthservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.medflow.medflowauthservice.entity.EmailVerificationToken;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByTokenHashAndUsedFalse(String tokenHash);
}
