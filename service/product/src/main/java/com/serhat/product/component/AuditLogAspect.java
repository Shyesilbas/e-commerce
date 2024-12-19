package com.serhat.product.component;

import com.serhat.product.dto.object.ProductDto;
import com.serhat.product.dto.request.AddProductRequest;
import com.serhat.product.dto.request.DeleteProductRequest;
import com.serhat.product.dto.request.UpdatePriceRequest;
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
        String details = "";

        try {
            details = switch (action) {
                case "addProduct" -> args[1] instanceof AddProductRequest request ?
                        "Product Code: " + request.productDto().productCode() : "";

                case "updatePrice" -> args[0] instanceof UpdatePriceRequest request ?
                        "Product Code: " + request.productCode() : "";

                case "productInformation" -> args[1] instanceof String productCode ?
                        "Product Code: " + productCode : "";

                case "deleteProduct" -> args[1] instanceof String productCode ?
                        "Product Code: " +productCode : "";

                default -> "";
            };
        } catch (Exception e) {
            details = "";
        }

        auditLogService.saveAuditLog(action, entity, details, time, username);
    }
}