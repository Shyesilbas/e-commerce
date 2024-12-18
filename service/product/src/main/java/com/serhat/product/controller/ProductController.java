package com.serhat.product.controller;

import com.serhat.product.dto.request.AddProductRequest;
import com.serhat.product.dto.response.AddProductResponse;
import com.serhat.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<AddProductResponse> addProduct(Principal principal , @Valid @RequestBody AddProductRequest request){
        return ResponseEntity.ok(productService.addProduct(principal,request));
    }
}
