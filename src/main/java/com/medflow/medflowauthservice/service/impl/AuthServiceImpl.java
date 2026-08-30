package com.medflow.medflowauthservice.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.Duration;
import java.util.Base64;
import java.util.HexFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.medflow.medflowauthservice.entity.EmailVerificationToken;
import com.medflow.medflowauthservice.entity.PasswordResetToken;
import com.medflow.medflowauthservice.entity.RoleName;
import com.medflow.medflowauthservice.entity.OtpPurpose;
import com.medflow.medflowauthservice.entity.User;
import com.medflow.medflowauthservice.repository.EmailVerificationTokenRepository;
import com.medflow.medflowauthservice.repository.PasswordResetTokenRepository;
import com.medflow.medflowauthservice.repository.RoleRepository;
import com.medflow.medflowauthservice.repository.UserRepository;
import com.medflow.medflowauthservice.service.AuthService;
import com.medflow.medflowauthservice.service.EmailService;
import com.medflow.medflowauthservice.service.OtpService;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepository users; private final RoleRepository roles; private final PasswordEncoder encoder; private final EmailService email;
    private final EmailVerificationTokenRepository verificationTokens; private final PasswordResetTokenRepository resetTokens; private final SecureRandom random = new SecureRandom();
    public AuthServiceImpl(UserRepository users, RoleRepository roles, PasswordEncoder encoder, EmailService email, EmailVerificationTokenRepository verificationTokens, PasswordResetTokenRepository resetTokens) { this.users = users; this.roles = roles; this.encoder = encoder; this.email = email; this.verificationTokens = verificationTokens; this.resetTokens = resetTokens; }
    @Transactional public User register(String fullName, String emailAddress, String password) {
        String emailValue = emailAddress.toLowerCase().trim(); if (users.existsByEmailIgnoreCase(emailValue)) throw new IllegalArgumentException("Unable to register account");
        User user = new User(fullName.trim(), emailValue, encoder.encode(password), roles.findByName(RoleName.ROLE_USER).orElseThrow()); users.save(user);
        String token = rawToken(); verificationTokens.save(new EmailVerificationToken(hash(token), user, Instant.now().plus(24, ChronoUnit.HOURS)));
        email.sendVerification(emailValue, token); email.sendWelcome(emailValue, fullName); return user;
    }
    @Transactional public void requestPasswordReset(String emailAddress) {
        log.info("Forgot password request received: emailDomain={}", domainOf(emailAddress));
        users.findByEmailIgnoreCase(emailAddress.trim()).ifPresentOrElse(user -> {
            String token = rawToken();
            resetTokens.save(new PasswordResetToken(hash(token), user, Instant.now().plus(30, ChronoUnit.MINUTES)));
            email.sendPasswordResetLink(user.getEmail(), token);
            log.info("Password reset link dispatch requested: emailDomain={}", domainOf(user.getEmail()));
        }, () -> log.info("Forgot password user lookup did not match: emailDomain={}", domainOf(emailAddress)));
    }
    @Transactional public boolean resetPassword(String token, String password) {
        var record = resetTokens.findByTokenHashAndUsedFalse(hash(token)).orElse(null);
        if (record == null || record.getExpiresAt().isBefore(Instant.now())) return false;
        record.getUser().setPassword(encoder.encode(password));
        record.markUsed();
        users.save(record.getUser());
        resetTokens.save(record);
        log.info("Password reset completed: emailDomain={}", domainOf(record.getUser().getEmail()));
        return true;
    }
    @Transactional public boolean verifyEmail(String token) {
        var record = verificationTokens.findByTokenHashAndUsedFalse(hash(token)).orElse(null); if (record == null || record.getExpiresAt().isBefore(Instant.now())) return false;
        record.getUser().markEmailVerified(); record.markUsed(); verificationTokens.save(record); users.save(record.getUser()); return true;
    }
    private String rawToken() { byte[] bytes = new byte[32]; random.nextBytes(bytes); return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes); }
    private String hash(String value) { try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8))); } catch (Exception exception) { throw new IllegalStateException(exception); } }
    private String domainOf(String address) { int separator = address.indexOf('@'); return separator > 0 ? address.substring(separator + 1) : "invalid"; }
}
