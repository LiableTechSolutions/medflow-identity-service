package com.medflow.medflowauthservice.controller;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.medflow.medflowauthservice.service.AuthService;

@Controller
@Validated
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }
    @PostMapping("/signup") public String register(@RequestParam @NotBlank String fullName, @RequestParam @Email String email, @RequestParam @Size(min = 10) String password, RedirectAttributes attributes) { authService.register(fullName, email, password); attributes.addFlashAttribute("message", "Account created. You can sign in now."); return "redirect:/login"; }
    @PostMapping("/forgot-password") public String forgot(@RequestParam @NotBlank @Email String email, RedirectAttributes attributes) { authService.requestPasswordReset(email); attributes.addFlashAttribute("message", "If the address exists, a password reset link has been sent."); return "redirect:/login"; }
    @PostMapping("/reset-password") public String reset(@RequestParam @NotBlank String token, @RequestParam @Size(min = 10) String password, @RequestParam @NotBlank String confirmPassword, RedirectAttributes attributes) { boolean reset = password.equals(confirmPassword) && authService.resetPassword(token, password); attributes.addFlashAttribute("message", reset ? "Password reset successfully." : "The reset link is invalid, expired, or the passwords do not match."); return "redirect:/login"; }
    @PostMapping("/verify-email") public String verify(@RequestParam String token, RedirectAttributes attributes) { attributes.addFlashAttribute("message", authService.verifyEmail(token) ? "Email verified successfully." : "Verification link is invalid or expired."); return "redirect:/login"; }
}
