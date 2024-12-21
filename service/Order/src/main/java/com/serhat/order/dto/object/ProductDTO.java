package com.serhat.order.dto.object;

import com.serhat.order.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductDTO(
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 100, message = "Name should be between 3 and 100 characters")
        String name,

        @NotBlank(message = "Product code is required")
        @Size(min = 3, max = 50, message = "Product code should be between 3 and 50 characters")
        String productCode,

        @NotNull(message = "Year is required")
        int year,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        BigDecimal price,

        @NotBlank(message = "Description is required")
        @Size(min = 2, max = 500, message = "Description should be between 2 and 500 characters")
        String description,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        int quantity,

        @NotNull(message = "Category is required")
        Category category,

        @NotBlank(message = "Country of origin is required")
        @Size(min = 2, max = 100, message = "Country of origin should be between 2 and 100 characters")
        String countryOfOrigin
) {
}
