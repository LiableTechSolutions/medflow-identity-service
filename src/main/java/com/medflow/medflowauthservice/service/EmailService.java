package com.medflow.medflowauthservice.service;

public interface EmailService {
    void sendOtp(String recipient, String code);
    void sendVerification(String recipient, String token);
    void sendPasswordResetOtp(String recipient, String otp, String expiryDescription);
    void sendPasswordResetLink(String recipient, String token);
    void sendWelcome(String recipient, String name);
}
