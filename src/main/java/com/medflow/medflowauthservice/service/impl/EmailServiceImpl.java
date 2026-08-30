package com.medflow.medflowauthservice.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
import com.medflow.medflowauthservice.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender mailSender;
    private final String from;
    private final String host;
    private final int port;
    private final String baseUrl;
    public EmailServiceImpl(JavaMailSender mailSender, @Value("${spring.mail.username:}") String from, @Value("${spring.mail.host:smtp.gmail.com}") String host, @Value("${spring.mail.port:587}") int port, @Value("${medflow.auth.base-url:http://localhost:8080}") String baseUrl) { this.mailSender = mailSender; this.from = from; this.host = host; this.port = port; this.baseUrl = baseUrl; }
    private void sendHtml(String recipient, String subject, String html) {
        if (from.isBlank()) {
            log.error("Email send failed: sender account is not configured; recipientDomain={}", domainOf(recipient));
            throw new IllegalStateException("Mail sender account is not configured");
        }
        log.info("Email send attempted: provider=smtp host={} port={} recipientDomain={} senderConfigured=true", host, port, domainOf(recipient));
        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(from, "MedFlow"); helper.setTo(recipient); helper.setSubject(subject); helper.setText(html, true);
            mailSender.send(message);
            log.info("Email send succeeded: recipientDomain={} subject={}", domainOf(recipient), subject);
        } catch (MailException | jakarta.mail.MessagingException | UnsupportedEncodingException exception) {
            log.error("Email send failed: recipientDomain={} subject={} errorType={}", domainOf(recipient), subject, exception.getClass().getSimpleName(), exception);
            throw new IllegalStateException("Email delivery failed", exception);
        }
    }
    public void sendOtp(String recipient, String code) { sendHtml(recipient, "Your MedFlow verification code", "<h2>MedFlow verification</h2><p>Your one-time code is <strong>" + code + "</strong>.</p>"); }
    public void sendPasswordResetOtp(String recipient, String otp, String expiryDescription) { sendHtml(recipient, "Your MedFlow password reset code", "<h2>Password reset requested</h2><p>Enter this one-time password reset code in MedFlow:</p><p><strong>" + otp + "</strong></p><p>This code expires in " + expiryDescription + " and can be used only once. If you did not request this, you can ignore this email.</p>"); }
    public void sendVerification(String recipient, String token) { sendHtml(recipient, "Verify your MedFlow email", "<h2>Verify your MedFlow email</h2><p>Use this verification token: <strong>" + token + "</strong>.</p>"); }
    public void sendPasswordResetLink(String recipient, String token) { sendHtml(recipient, "Reset your MedFlow password", "<h2>Password reset requested</h2><p><a href=\"" + baseUrl + "/reset-password?token=" + token + "\">Create a new password</a></p><p>This link expires in 30 minutes and can be used only once. If you did not request this, you can ignore this email.</p>"); }
    public void sendWelcome(String recipient, String name) { sendHtml(recipient, "Welcome to MedFlow", "<h2>Welcome to MedFlow</h2><p>Welcome, " + name + ".</p>"); }
    private String domainOf(String address) { int separator = address.indexOf('@'); return separator > 0 ? address.substring(separator + 1) : "invalid"; }
}
