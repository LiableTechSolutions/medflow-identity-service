package com.medflow.medflowauthservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.medflow.medflowauthservice.security.PolicyAuthenticationProvider;
import com.medflow.medflowauthservice.security.PolicyAuthenticationSuccessHandler;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final PolicyAuthenticationProvider authenticationProvider;
    private final PolicyAuthenticationSuccessHandler successHandler;
    public SecurityConfig(PolicyAuthenticationProvider authenticationProvider, PolicyAuthenticationSuccessHandler successHandler) { this.authenticationProvider = authenticationProvider; this.successHandler = successHandler; }
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider)
            .authorizeHttpRequests(auth -> auth.requestMatchers("/", "/login", "/register", "/signup", "/forgot-password", "/reset-password", "/verify-email", "/login/otp/**", "/css/**", "/js/**").permitAll().requestMatchers("/admin/**").hasRole("ADMIN").anyRequest().authenticated())
            .formLogin(login -> login.loginPage("/login").successHandler(successHandler).permitAll())
            .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").invalidateHttpSession(true).deleteCookies("JSESSIONID"))
            .sessionManagement(session -> session.maximumSessions(1));
        return http.build();
    }
}
