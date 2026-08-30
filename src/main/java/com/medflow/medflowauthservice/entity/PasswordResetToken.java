package com.medflow.medflowauthservice.entity;

import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String tokenHash;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private User user;
    @Column(nullable = false) private Instant expiresAt;
    private boolean used;
    protected PasswordResetToken() { }
    public PasswordResetToken(String tokenHash, User user, Instant expiresAt) { this.tokenHash = tokenHash; this.user = user; this.expiresAt = expiresAt; }
    public String getTokenHash() { return tokenHash; }
    public User getUser() { return user; }
    public Instant getExpiresAt() { return expiresAt; }
    public boolean isUsed() { return used; }
    public void markUsed() { used = true; }
}
