package com.medflow.medflowauthservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.medflow.medflowauthservice.entity.AuthenticationPolicy;
import com.medflow.medflowauthservice.entity.AuthPolicyType;

public interface AuthenticationPolicyRepository extends JpaRepository<AuthenticationPolicy, Long> {
    Optional<AuthenticationPolicy> findByType(AuthPolicyType type);
    Optional<AuthenticationPolicy> findByActiveTrue();
}
