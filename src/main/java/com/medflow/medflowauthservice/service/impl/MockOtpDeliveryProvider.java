package com.medflow.medflowauthservice.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.medflow.medflowauthservice.entity.OtpPurpose;
import com.medflow.medflowauthservice.service.OtpDeliveryProvider;

@Component
@ConditionalOnProperty(name = "medflow.otp.provider", havingValue = "mock")
public class MockOtpDeliveryProvider implements OtpDeliveryProvider {
    private static final Logger log = LoggerFactory.getLogger(MockOtpDeliveryProvider.class);

    @Override
    public void deliver(String recipient, String code, OtpPurpose purpose) {
        log.info("Mock OTP delivery selected: purpose={} recipientDomain={}", purpose, domainOf(recipient));
    }

    private String domainOf(String address) {
        int separator = address.indexOf('@');
        return separator > 0 ? address.substring(separator + 1) : "invalid";
    }
}