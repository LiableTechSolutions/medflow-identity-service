package com.medflow.medflowauthservice.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.medflow.medflowauthservice.entity.AuthPolicyType;
import com.medflow.medflowauthservice.repository.AuthenticationPolicyRepository;
import com.medflow.medflowauthservice.service.PolicyService;

@Service
public class PolicyServiceImpl implements PolicyService {
    private final AuthenticationPolicyRepository repository; private final AuthPolicyType defaultPolicy;
    public PolicyServiceImpl(AuthenticationPolicyRepository repository, @Value("${medflow.auth.default-policy:PASSWORD_ONLY}") AuthPolicyType defaultPolicy) { this.repository = repository; this.defaultPolicy = defaultPolicy; }
    public AuthPolicyType activePolicy() { return repository.findByActiveTrue().map(p -> p.getType()).orElse(defaultPolicy); }
    public boolean allowsPassword() { return activePolicy() != AuthPolicyType.OTP_ONLY; }
    public boolean allowsOtp() { return activePolicy() != AuthPolicyType.PASSWORD_ONLY; }
}
