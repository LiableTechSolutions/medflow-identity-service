package com.medflow.medflowauthservice.entity;

import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String action;
    private String email;
    private String ipAddress;
    @Column(nullable = false) private Instant createdAt;
    protected AuditLog() { }
    public AuditLog(String action, String email, String ipAddress) { this.action = action; this.email = email; this.ipAddress = ipAddress; this.createdAt = Instant.now(); }
    public String getAction() { return action; }
    public String getEmail() { return email; }
    public String getIpAddress() { return ipAddress; }
    public Instant getCreatedAt() { return createdAt; }
}
