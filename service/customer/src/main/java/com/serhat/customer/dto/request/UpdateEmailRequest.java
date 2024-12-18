package com.serhat.customer.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateEmailRequest(
        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is required")
        String newEmail
) {
}
