package com.medflow.medflowauthservice.service;

import com.medflow.medflowauthservice.entity.OtpPurpose;

public interface OtpService {
    void issue(String email, OtpPurpose purpose);
    boolean verify(String email, OtpPurpose purpose, String code);
}
