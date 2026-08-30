package com.medflow.medflowauthservice.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.medflow.medflowauthservice.entity.AuditLog;
import com.medflow.medflowauthservice.entity.OtpCode;
import com.medflow.medflowauthservice.entity.OtpPurpose;
import com.medflow.medflowauthservice.repository.AuditLogRepository;
import com.medflow.medflowauthservice.repository.OtpCodeRepository;
import com.medflow.medflowauthservice.service.EmailService;

class OtpServiceImplTest {
    private OtpCodeRepository repository;
    private PasswordEncoder encoder;
    private EmailService email;
    private AuditLogRepository auditLogs;
    private OtpServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(OtpCodeRepository.class);
        encoder = mock(PasswordEncoder.class);
        email = mock(EmailService.class);
        auditLogs = mock(AuditLogRepository.class);
        when(encoder.encode(any(CharSequence.class))).thenAnswer(invocation -> "hash:" + invocation.getArgument(0));
        service = new OtpServiceImpl(repository, encoder, email, auditLogs, 6, Duration.ofMinutes(10), Duration.ZERO, 3);
    }

    @Test
    void issuePersistsPasswordResetOtpAndSendsToRecipient() {
        service.issue("user@example.com", OtpPurpose.PASSWORD_RESET);

        ArgumentCaptor<OtpCode> captor = ArgumentCaptor.forClass(OtpCode.class);
        verify(repository).save(captor.capture());
        OtpCode saved = captor.getValue();
        assertEquals(OtpPurpose.PASSWORD_RESET, saved.getPurpose());
        assertEquals("user@example.com", saved.getEmail());
        assertTrue(saved.getCodeHash().startsWith("hash:"));
        verify(email).sendPasswordResetOtp(eq("user@example.com"), matches("\\d{6}"), eq("PT10M"));
    }

    @Test
    void incorrectOtpIncrementsAttemptsAndDoesNotConsumeOtp() {
        OtpCode otp = new OtpCode("user@example.com", "hash:123456", OtpPurpose.PASSWORD_RESET, Instant.now().plusSeconds(60), Instant.now());
        when(repository.findTopByEmailIgnoreCaseAndPurposeAndUsedFalseOrderByCreatedAtDesc("user@example.com", OtpPurpose.PASSWORD_RESET)).thenReturn(Optional.of(otp));
        when(encoder.matches("000000", "hash:123456")).thenReturn(false);

        assertFalse(service.verify("user@example.com", OtpPurpose.PASSWORD_RESET, "000000"));
        assertEquals(1, otp.getAttempts());
        assertFalse(otp.isUsed());
    }

    @Test
    void expiredOtpIsRejected() {
        OtpCode otp = new OtpCode("user@example.com", "hash:123456", OtpPurpose.PASSWORD_RESET, Instant.now().minusSeconds(1), Instant.now().minusSeconds(60));
        when(repository.findTopByEmailIgnoreCaseAndPurposeAndUsedFalseOrderByCreatedAtDesc(anyString(), eq(OtpPurpose.PASSWORD_RESET))).thenReturn(Optional.of(otp));

        assertFalse(service.verify("user@example.com", OtpPurpose.PASSWORD_RESET, "123456"));
        verify(encoder, never()).matches(anyString(), anyString());
    }

    @Test
    void successfulOtpIsConsumedAndCannotBeReused() {
        OtpCode otp = new OtpCode("user@example.com", "hash:123456", OtpPurpose.PASSWORD_RESET, Instant.now().plusSeconds(60), Instant.now());
        when(repository.findTopByEmailIgnoreCaseAndPurposeAndUsedFalseOrderByCreatedAtDesc("user@example.com", OtpPurpose.PASSWORD_RESET)).thenReturn(Optional.of(otp));
        when(encoder.matches("123456", "hash:123456")).thenReturn(true);

        assertTrue(service.verify("user@example.com", OtpPurpose.PASSWORD_RESET, "123456"));
        assertTrue(otp.isUsed());
        assertEquals(1, otp.getAttempts());
    }

    @Test
    void maximumAttemptsAreEnforced() {
        OtpCode otp = new OtpCode("user@example.com", "hash:123456", OtpPurpose.PASSWORD_RESET, Instant.now().plusSeconds(60), Instant.now());
        otp.incrementAttempts(); otp.incrementAttempts(); otp.incrementAttempts();
        when(repository.findTopByEmailIgnoreCaseAndPurposeAndUsedFalseOrderByCreatedAtDesc(anyString(), eq(OtpPurpose.PASSWORD_RESET))).thenReturn(Optional.of(otp));

        assertFalse(service.verify("user@example.com", OtpPurpose.PASSWORD_RESET, "123456"));
        verify(encoder, never()).matches(anyString(), anyString());
    }
}