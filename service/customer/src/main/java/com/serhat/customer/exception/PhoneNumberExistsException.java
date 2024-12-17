package com.serhat.customer.exception;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class PhoneNumberExistsException extends RuntimeException {
    public PhoneNumberExistsException(
            @NotBlank(message = "Phone number is required")
            @Pattern(regexp = "^\\d{4} \\d{3} \\d{2} \\d{2}$",
                    message = "Phone number must match the pattern xxxx xxx xx xx") String s) {
        super(s);
    }
}
