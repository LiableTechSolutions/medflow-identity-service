package com.medflow.medflowauthservice.entity;

import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Table(name = "authentication_policies")
public class AuthenticationPolicy {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Enumerated(EnumType.STRING) @Column(nullable = false, unique = true) private AuthPolicyType type;
    @Column(nullable = false) private boolean active;
    @Column(nullable = false) private Instant updatedAt;
    protected AuthenticationPolicy() { }
    public AuthenticationPolicy(AuthPolicyType type, boolean active) { this.type = type; this.active = active; this.updatedAt = Instant.now(); }
    public AuthPolicyType getType() { return type; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; this.updatedAt = Instant.now(); }
}
