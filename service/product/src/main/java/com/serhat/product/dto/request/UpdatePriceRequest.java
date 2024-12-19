package com.serhat.product.dto.request;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdatePriceRequest(
        String productCode,
        @Positive(message = "Price Must be positive.")
        BigDecimal updatedPrice
) {
}
