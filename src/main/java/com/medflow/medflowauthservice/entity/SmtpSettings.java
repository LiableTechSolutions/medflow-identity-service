package com.medflow.medflowauthservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "smtp_settings")
public class SmtpSettings {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String host;
    private int port;
    private String username;
    private boolean enabled;
    protected SmtpSettings() { }
    public SmtpSettings(String host, int port, String username, boolean enabled) { this.host = host; this.port = port; this.username = username; this.enabled = enabled; }
    public Long getId() { return id; }
    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getUsername() { return username; }
    public boolean isEnabled() { return enabled; }
}
