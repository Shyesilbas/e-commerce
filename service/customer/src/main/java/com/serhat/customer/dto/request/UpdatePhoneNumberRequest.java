package com.serhat.customer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdatePhoneNumberRequest(
        @NotBlank(message = "New Phone number is required")
        @Pattern(regexp = "^\\d{4} \\d{3} \\d{2} \\d{2}$", message = "Phone number must match the pattern xxxx xxx xx xx")
        String newPhoneNumber
) {
}
