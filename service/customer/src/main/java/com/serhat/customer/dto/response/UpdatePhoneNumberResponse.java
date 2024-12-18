package com.serhat.customer.dto.response;

public record UpdatePhoneNumberResponse(
        String message,
        String newPhoneNumber
) {
}
