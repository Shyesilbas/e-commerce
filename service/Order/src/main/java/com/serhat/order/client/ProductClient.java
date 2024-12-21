package com.serhat.order.client;

import com.serhat.order.dto.object.ProductDTO;
import com.serhat.order.exception.ProductNotFoundException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service",url = "http://localhost:8060/api/products")
public interface ProductClient {

    @GetMapping("/info")
    ProductDTO productInfo(@RequestParam String productCode);

    @PutMapping("/updateQuantityAfterOrder")
    void updateProductQuantity(@RequestParam String productCode, @RequestParam int decrement , @RequestHeader("Authorization") String authorizationHeader);

}
