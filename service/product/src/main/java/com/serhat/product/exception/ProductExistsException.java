package com.serhat.product.exception;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProductExistsException extends RuntimeException {
    public ProductExistsException(@NotBlank(message = "Product code is required") @Size(min = 3, max = 50, message = "Product code should be between 3 and 50 characters") String s) {
        super(s);
    }
}
