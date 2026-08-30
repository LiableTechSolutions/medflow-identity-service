package com.medflow.medflowauthservice.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.medflow.medflowauthservice.service.PolicyService;

@Component
public class PolicyAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService users;
    private final PasswordEncoder encoder;
    private final PolicyService policyService;

    public PolicyAuthenticationProvider(UserDetailsService users, @Lazy PasswordEncoder encoder, PolicyService policyService) {
        this.users = users;
        this.encoder = encoder;
        this.policyService = policyService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (!policyService.allowsPassword()) {
            throw new BadCredentialsException("Password authentication is disabled");
        }
        var principal = users.loadUserByUsername(authentication.getName().trim());
        if (!encoder.matches(authentication.getCredentials().toString(), principal.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return UsernamePasswordAuthenticationToken.authenticated(principal, null, principal.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}