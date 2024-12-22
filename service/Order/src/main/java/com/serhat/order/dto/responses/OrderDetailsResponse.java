package com.serhat.order.dto.responses;

import com.serhat.order.dto.object.AddressDTO;
import com.serhat.order.entity.Status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailsResponse(
        Integer orderId,
        Integer totalQuantity,
        BigDecimal totalPrice,
        LocalDateTime orderDate,
        Status status,
        AddressDTO address,
        List<OrderProductResponse> products
) {
}
