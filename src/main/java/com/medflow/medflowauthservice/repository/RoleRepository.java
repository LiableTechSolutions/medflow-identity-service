package com.medflow.medflowauthservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.medflow.medflowauthservice.entity.Role;
import com.medflow.medflowauthservice.entity.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
