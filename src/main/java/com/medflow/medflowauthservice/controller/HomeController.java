package com.medflow.medflowauthservice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import com.medflow.medflowauthservice.service.PolicyService;

@Controller
public class HomeController {
    private final PolicyService policyService;
    public HomeController(PolicyService policyService) { this.policyService = policyService; }
    @GetMapping("/") public String home() { return "redirect:/login"; }
    @GetMapping("/dashboard") public String dashboard() { return "dashboard"; }
    @GetMapping("/login") public String login(Model model) { model.addAttribute("policy", policyService.activePolicy()); return "auth/login"; }
    @GetMapping("/signup") public String signup() { return "auth/signup"; }
    @GetMapping("/forgot-password") public String forgotPassword() { return "auth/forgot-password"; }
    @GetMapping("/reset-password") public String resetPassword() { return "auth/reset-password"; }
    @GetMapping("/password-reset-otp") public String passwordResetOtp() { return "auth/password-reset-otp"; }
    @GetMapping("/verify-email") public String verifyEmail() { return "auth/verify-email"; }
    @GetMapping("/profile") public String profile() { return "profile"; }
    @GetMapping("/settings") public String settings() { return "settings"; }
    @GetMapping("/admin") public String admin() { return "admin/dashboard"; }
    @GetMapping("/admin/users") public String users() { return "admin/users"; }
    @GetMapping("/admin/roles") public String roles() { return "admin/roles"; }
    @GetMapping("/admin/policy") public String policy() { return "admin/policy"; }
    @GetMapping("/admin/smtp") public String smtp() { return "admin/smtp"; }
    @GetMapping("/admin/audit-logs") public String auditLogs() { return "admin/audit-logs"; }
}
