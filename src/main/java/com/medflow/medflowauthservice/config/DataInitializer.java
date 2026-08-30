package com.medflow.medflowauthservice.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.medflow.medflowauthservice.entity.Role;
import com.medflow.medflowauthservice.entity.RoleName;
import com.medflow.medflowauthservice.repository.RoleRepository;

@Configuration
public class DataInitializer {
    @Bean CommandLineRunner initializeRoles(RoleRepository roles) { return args -> { for (RoleName name : RoleName.values()) roles.findByName(name).orElseGet(() -> roles.save(new Role(name))); }; }
}
