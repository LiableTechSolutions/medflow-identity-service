package com.medflow.medflowauthservice.service;

import com.medflow.medflowauthservice.entity.User;

public interface AuthService {
    User register(String fullName, String email, String password);
    void requestPasswordReset(String email);
    boolean resetPassword(String token, String password);
    boolean verifyEmail(String token);
}
