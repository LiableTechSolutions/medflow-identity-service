package com.medflow.medflowauthservice.entity;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "otp_codes")
public class OtpCode {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String email;
    private String codeHash;
    @Enumerated(EnumType.STRING) private OtpPurpose purpose;
    private Instant expiresAt;
    private Instant createdAt;
    private Instant lastSentAt;
    private int attempts;
    private boolean used;

    protected OtpCode() { }
    public OtpCode(String email, String codeHash, OtpPurpose purpose, Instant expiresAt, Instant sentAt) {
        this.email = email; this.codeHash = codeHash; this.purpose = purpose; this.expiresAt = expiresAt; this.createdAt = sentAt; this.lastSentAt = sentAt;
    }
    public String getEmail() { return email; }
    public String getCodeHash() { return codeHash; }
    public OtpPurpose getPurpose() { return purpose; }
    public Instant getExpiresAt() { return expiresAt; }
    public Instant getLastSentAt() { return lastSentAt; }
    public int getAttempts() { return attempts; }
    public boolean isUsed() { return used; }
    public void incrementAttempts() { attempts++; }
    public void markUsed() { used = true; }
}
