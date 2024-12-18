package com.serhat.product.component;

import com.serhat.product.dto.request.AddProductRequest;
import com.serhat.product.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {
    private final AuditLogService auditLogService;

    @Pointcut("execution(* com.serhat.product.controller.*.*(..))")

    public void controllerMethods() {}

    @Before("controllerMethods()")
    public void logAudit(JoinPoint joinPoint) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String action = joinPoint.getSignature().getName();
        String entity = joinPoint.getTarget().getClass().getSimpleName();
        LocalDateTime time = LocalDateTime.now();
        Object[] args = joinPoint.getArgs();
        String details = "Method " + action;

        if ("addProduct".equals(action) && args.length > 1 && args[1] instanceof AddProductRequest request) {
            String productCode = request.productDto().productCode();
            details = "Method " + action + " - Product Code: " + productCode;
        }

        auditLogService.saveAuditLog(action, entity, details, time, username);
    }

}
