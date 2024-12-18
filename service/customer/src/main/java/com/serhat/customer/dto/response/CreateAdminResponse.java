package com.serhat.customer.dto.response;

public record CreateAdminResponse(
        String message,
        String email,
        String name,
        String surname
) {
}
