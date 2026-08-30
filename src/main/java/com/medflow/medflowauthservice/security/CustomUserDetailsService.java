package com.medflow.medflowauthservice.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Service;
import com.medflow.medflowauthservice.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository users;
    public CustomUserDetailsService(UserRepository users) { this.users = users; }
    @Override public UserDetails loadUserByUsername(String username) {
        var user = users.findByEmailIgnoreCase(username.trim()).orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));
        if (!user.isEmailVerified()) throw new DisabledException("Email address is not verified");
        return user;
    }
}
