package com.serhat.product.service;


import com.serhat.product.entity.AuditLog;
import com.serhat.product.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void saveAuditLog(String action, String entity, String details,LocalDateTime time ,String username) {
        AuditLog log = AuditLog.builder()
                .action(action)
                .entity(entity)
                .details(details)
                .timestamp(time)
                .username(username)
                .build();
        auditLogRepository.save(log);
    }
}