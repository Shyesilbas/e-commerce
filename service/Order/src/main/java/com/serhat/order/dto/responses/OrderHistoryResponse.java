package com.serhat.order.dto.responses;

import com.serhat.order.dto.object.OrderProductDto;
import com.serhat.order.entity.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderHistoryResponse(
        Integer orderId,
        Integer totalQuantity,
        BigDecimal totalPrice,
        LocalDateTime orderDate,
        Status status,
        Integer addressId,
        List<OrderProductResponse> products
) {
}
