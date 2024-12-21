package com.serhat.product.dto.object;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PriceHistoryDTO(
        String product_code,
        BigDecimal original_price,
        BigDecimal updated_price,
        String price_difference,
        LocalDateTime valid_from,
        LocalDateTime valid_to

) {
}
