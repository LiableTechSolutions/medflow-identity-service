package com.medflow.medflowauthservice.service.impl;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.medflow.medflowauthservice.entity.OtpCode;
import com.medflow.medflowauthservice.entity.OtpPurpose;
import com.medflow.medflowauthservice.entity.AuditLog;
import com.medflow.medflowauthservice.repository.AuditLogRepository;
import com.medflow.medflowauthservice.repository.OtpCodeRepository;
import com.medflow.medflowauthservice.service.OtpDeliveryProvider;
import com.medflow.medflowauthservice.service.EmailService;
import com.medflow.medflowauthservice.service.OtpService;

@Service
public class OtpServiceImpl implements OtpService {
    private final OtpCodeRepository repository; private final PasswordEncoder encoder; private final OtpDeliveryProvider deliveryProvider; private final AuditLogRepository auditLogs;
    private final SecureRandom random = new SecureRandom(); private final int length; private final Duration expiry; private final Duration cooldown; private final int maxAttempts;
    @Autowired public OtpServiceImpl(OtpCodeRepository repository, @Lazy PasswordEncoder encoder, OtpDeliveryProvider deliveryProvider, AuditLogRepository auditLogs, @Value("${medflow.otp.length:6}") int length, @Value("${medflow.otp.expiry:PT10M}") Duration expiry, @Value("${medflow.otp.resend-cooldown:PT1M}") Duration cooldown, @Value("${medflow.otp.max-attempts:5}") int maxAttempts) { this.repository = repository; this.encoder = encoder; this.deliveryProvider = deliveryProvider; this.auditLogs = auditLogs; this.length = length; this.expiry = expiry; this.cooldown = cooldown; this.maxAttempts = maxAttempts; }
    public OtpServiceImpl(OtpCodeRepository repository, PasswordEncoder encoder, EmailService emailService, AuditLogRepository auditLogs, int length, Duration expiry, Duration cooldown, int maxAttempts) { this(repository, encoder, (recipient, code, purpose) -> { if (purpose == OtpPurpose.PASSWORD_RESET) emailService.sendPasswordResetOtp(recipient, code, expiry.toString()); else emailService.sendOtp(recipient, code); }, auditLogs, length, expiry, cooldown, maxAttempts); }
    @Transactional public void issue(String email, OtpPurpose purpose) {
        var current = repository.findTopByEmailIgnoreCaseAndPurposeAndUsedFalseOrderByCreatedAtDesc(email, purpose);
        if (current.isPresent() && current.get().getLastSentAt().plus(cooldown).isAfter(Instant.now())) throw new IllegalStateException("OTP resend cooldown is active");
        StringBuilder code = new StringBuilder(); for (int i = 0; i < length; i++) code.append(random.nextInt(10));
        repository.save(new OtpCode(email, encoder.encode(code), purpose, Instant.now().plus(expiry), Instant.now()));
        auditLogs.save(new AuditLog("OTP_ISSUED_" + purpose, email, null));
        deliveryProvider.deliver(email, code.toString(), purpose);
    }
    @Transactional public boolean verify(String email, OtpPurpose purpose, String code) {
        var otp = repository.findTopByEmailIgnoreCaseAndPurposeAndUsedFalseOrderByCreatedAtDesc(email, purpose);
        if (otp.isEmpty() || otp.get().getExpiresAt().isBefore(Instant.now()) || otp.get().getAttempts() >= maxAttempts) return false;
        otp.get().incrementAttempts(); if (!encoder.matches(code, otp.get().getCodeHash())) { repository.save(otp.get()); auditLogs.save(new AuditLog("OTP_FAILED_" + purpose, email, null)); return false; }
        otp.get().markUsed(); repository.save(otp.get()); auditLogs.save(new AuditLog("OTP_VERIFIED_" + purpose, email, null)); return true;
    }
}
