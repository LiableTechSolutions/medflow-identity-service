package com.medflow.medflowauthservice.security;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.medflow.medflowauthservice.entity.OtpPurpose;
import com.medflow.medflowauthservice.service.OtpService;
import com.medflow.medflowauthservice.service.PolicyService;

@Component
public class PolicyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final PolicyService policyService;
    private final OtpService otpService;

    public PolicyAuthenticationSuccessHandler(PolicyService policyService, OtpService otpService) {
        this.policyService = policyService;
        this.otpService = otpService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (policyService.activePolicy().name().equals("PASSWORD_AND_OTP")) {
            String email = authentication.getName();
            otpService.issue(email, OtpPurpose.LOGIN);
            request.changeSessionId();
            request.getSession(true).setAttribute("pendingOtpEmail", email);
            SecurityContextHolder.clearContext();
            response.sendRedirect("/login?otpRequired=true");
            return;
        }
        response.sendRedirect("/dashboard");
    }
}