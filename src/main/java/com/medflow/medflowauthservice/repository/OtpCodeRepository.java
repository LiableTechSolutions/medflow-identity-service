package com.medflow.medflowauthservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.medflow.medflowauthservice.entity.OtpCode;
import com.medflow.medflowauthservice.entity.OtpPurpose;

public interface OtpCodeRepository extends JpaRepository<OtpCode, Long> {
    Optional<OtpCode> findTopByEmailIgnoreCaseAndPurposeAndUsedFalseOrderByCreatedAtDesc(String email, OtpPurpose purpose);
}
