package com.medflow.medflowauthservice.service;

import com.medflow.medflowauthservice.entity.OtpPurpose;

public interface OtpDeliveryProvider {
    void deliver(String recipient, String code, OtpPurpose purpose);
}