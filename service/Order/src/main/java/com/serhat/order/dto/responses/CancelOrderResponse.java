package com.serhat.order.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CancelOrderResponse(
        String message,
        BigDecimal refund,
        LocalDateTime cancellationDate
) {
}
