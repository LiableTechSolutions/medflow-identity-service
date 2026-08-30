package com.medflow.medflowauthservice.service.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.medflow.medflowauthservice.entity.OtpPurpose;
import com.medflow.medflowauthservice.service.EmailService;
import com.medflow.medflowauthservice.service.OtpDeliveryProvider;

@Component
@ConditionalOnProperty(name = "medflow.otp.provider", havingValue = "smtp", matchIfMissing = true)
public class SmtpOtpDeliveryProvider implements OtpDeliveryProvider {
    private final EmailService emailService;

    public SmtpOtpDeliveryProvider(EmailService emailService) {
        this.emailService = emailService;
    }

    @Override
    public void deliver(String recipient, String code, OtpPurpose purpose) {
        emailService.sendOtp(recipient, code);
    }
}