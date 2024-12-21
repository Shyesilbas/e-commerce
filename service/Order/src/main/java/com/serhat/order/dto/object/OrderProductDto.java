package com.serhat.order.dto.object;

import java.math.BigDecimal;

public record OrderProductDto(
        String productName,
        String productCode,
        Integer quantity,
        BigDecimal totalPrice
) {
}
