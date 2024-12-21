package com.serhat.product.dto.response;

public record UpdateQuantityResponse(
        String message,

        String productCode,

        int newQuantity
) {
}
