package com.serhat.customer.exception;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailExistsException extends RuntimeException {
    public EmailExistsException(
            @Email(message = "Email should be valid")
            @NotBlank(message = "Email is required") String s) {
        super(s);
    }
}
