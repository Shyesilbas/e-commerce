package com.serhat.product.dto.request;

public record UpdateQuantityRequest(
        String productCode,
        int newQuantity
) {
}
