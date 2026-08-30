package com.medflow.medflowauthservice.service;

import com.medflow.medflowauthservice.entity.AuthPolicyType;

public interface PolicyService {
    AuthPolicyType activePolicy();
    boolean allowsPassword();
    boolean allowsOtp();
}
