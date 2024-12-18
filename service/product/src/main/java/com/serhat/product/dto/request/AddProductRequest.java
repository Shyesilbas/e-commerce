package com.serhat.product.dto.request;

import com.serhat.product.dto.object.ProductDto;

public record AddProductRequest(
        ProductDto productDto
) {
}
