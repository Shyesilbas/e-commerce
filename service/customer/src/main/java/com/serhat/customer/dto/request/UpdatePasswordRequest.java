package com.serhat.customer.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

public record UpdatePasswordRequest(
        @NotBlank(message = "New Password cannot be empty")
        String newPassword,
        @NotBlank(message = "Old Password cannot be empty")
        String oldPassword
) {
}
