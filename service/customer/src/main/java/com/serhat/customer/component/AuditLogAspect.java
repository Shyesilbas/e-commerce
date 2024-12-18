package com.serhat.customer.component;

import com.serhat.customer.dto.request.CreateAdminRequest;
import com.serhat.customer.dto.request.CreateCustomerRequest;
import com.serhat.customer.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {
    private final AuditLogService auditLogService;

    @Pointcut("execution(* com.serhat.customer.controller.*.*(..))")
    public void controllerMethods() {}

    @Before("controllerMethods()")
    public void logAudit(JoinPoint joinPoint) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;

        if (authentication != null && authentication.getName() != null) {
            username = authentication.getName();
        } else {
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (arg instanceof CreateCustomerRequest) {
                    username = ((CreateCustomerRequest) arg).email();
                    break;
                } else if (arg instanceof CreateAdminRequest) {
                    username = ((CreateAdminRequest) arg).email();
                    break;
                }
            }
        }

        String action = joinPoint.getSignature().getName();
        String entity = joinPoint.getTarget().getClass().getSimpleName();
        LocalDateTime time = LocalDateTime.now();
        String details = "Method " + action;

        auditLogService.saveAuditLog(action, entity, details, time, username);
    }

}
