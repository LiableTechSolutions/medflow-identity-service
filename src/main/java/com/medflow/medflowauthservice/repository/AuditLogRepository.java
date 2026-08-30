package com.medflow.medflowauthservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.medflow.medflowauthservice.entity.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> { }
