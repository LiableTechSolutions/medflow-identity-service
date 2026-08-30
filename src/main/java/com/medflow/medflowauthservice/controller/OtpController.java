package com.medflow.medflowauthservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.medflow.medflowauthservice.entity.OtpPurpose;
import com.medflow.medflowauthservice.service.OtpService;
import com.medflow.medflowauthservice.service.PolicyService;
import com.medflow.medflowauthservice.service.AuthService;
import com.medflow.medflowauthservice.security.CustomUserDetailsService;

@Controller
@Validated
public class OtpController {
    private final OtpService otpService; private final PolicyService policyService; private final CustomUserDetailsService users; private final AuthService authService;
    public OtpController(OtpService otpService, PolicyService policyService, CustomUserDetailsService users, AuthService authService) { this.otpService = otpService; this.policyService = policyService; this.users = users; this.authService = authService; }
    @PostMapping("/login/otp/request") public String request(@RequestParam @Email String email, RedirectAttributes attributes) { if (!policyService.allowsOtp()) { attributes.addFlashAttribute("message", "OTP authentication is disabled."); return "redirect:/login"; } otpService.issue(email.trim(), OtpPurpose.LOGIN); attributes.addFlashAttribute("message", "A code has been sent if the account is eligible."); return "redirect:/login"; }
    @PostMapping("/login/otp/verify") public String verify(@RequestParam @Email String email, @RequestParam @NotBlank String code, HttpServletRequest request, RedirectAttributes attributes) { Object pending = request.getSession(false) == null ? null : request.getSession(false).getAttribute("pendingOtpEmail"); boolean pendingMatches = pending == null || email.trim().equalsIgnoreCase(pending.toString()); if (policyService.allowsOtp() && pendingMatches && otpService.verify(email.trim(), OtpPurpose.LOGIN, code)) { var principal = users.loadUserByUsername(email.trim()); var authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()); var context = SecurityContextHolder.createEmptyContext(); context.setAuthentication(authentication); SecurityContextHolder.setContext(context); request.changeSessionId(); request.getSession(true).removeAttribute("pendingOtpEmail"); request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", context); return "redirect:/dashboard"; } attributes.addFlashAttribute("message", "The code is invalid or expired."); return "redirect:/login"; }
}
