package com.medflow.medflowauthservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.medflow.medflowauthservice.entity.PasswordResetToken;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHashAndUsedFalse(String tokenHash);
}
