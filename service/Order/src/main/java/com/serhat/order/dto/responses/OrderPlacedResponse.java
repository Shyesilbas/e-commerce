package com.serhat.order.dto.responses;

import com.serhat.order.dto.object.AddressDTO;
import com.serhat.order.dto.object.OrderProductDto;
import com.serhat.order.entity.OrderProduct;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderPlacedResponse(
        String message,
        String email,
        LocalDateTime orderDate,
        List<OrderProductDto> orderProducts,
        BigDecimal price,
        AddressDTO address
) {
}
