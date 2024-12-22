package com.serhat.order.dto.responses;

import java.math.BigDecimal;

public record OrderProductResponse(
        String productName,
        String productCode,
        Integer quantity,
        BigDecimal totalPrice
) {
}
