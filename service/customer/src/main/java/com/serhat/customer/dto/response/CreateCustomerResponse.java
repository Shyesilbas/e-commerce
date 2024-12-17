package com.serhat.customer.dto.response;

public record CreateCustomerResponse(
        String message,
        String email,
        String name,
        String surname
) {
}
