package com.serhat.product.controller;

import com.serhat.product.dto.object.ProductDto;
import com.serhat.product.dto.request.AddProductRequest;
import com.serhat.product.dto.request.UpdatePriceRequest;
import com.serhat.product.dto.response.AddProductResponse;
import com.serhat.product.dto.response.DeleteProductResponse;
import com.serhat.product.dto.response.UpdatePriceResponse;
import com.serhat.product.entity.Category;
import com.serhat.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/add")
    public ResponseEntity<AddProductResponse> addProduct(Principal principal , @Valid @RequestBody AddProductRequest request){
        return ResponseEntity.ok(productService.addProduct(principal,request));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<DeleteProductResponse> deleteProduct(Principal principal ,@Valid @RequestParam String productCode){
        return ResponseEntity.ok(productService.deleteProduct(principal , productCode));
    }

    @PutMapping("/updatePrice")
    public ResponseEntity<UpdatePriceResponse> updatePrice (@RequestBody UpdatePriceRequest request){
        return ResponseEntity.ok(productService.updatePrice(request));
    }

    @GetMapping("/info")
    public ResponseEntity<ProductDto> productInformation(Principal principal,@RequestParam String productCode){
        return ResponseEntity.ok(productService.productInfo(principal,productCode));
    }

    @GetMapping("/listByCategory")
    public ResponseEntity<List<ProductDto>> listByCategory(@RequestParam Category category){
        return ResponseEntity.ok(productService.listProductByCategory(category));
    }
    @GetMapping("/listByPriceRange")
    public ResponseEntity<List<ProductDto>> listByPriceRange(@RequestParam BigDecimal minPrice , @RequestParam BigDecimal maxPrice){
        return ResponseEntity.ok(productService.listProductByPriceRange(minPrice, maxPrice));
    }

    @GetMapping("/totalProductForPriceRange")
    public ResponseEntity<Long> totalProductForPriceRange (@RequestParam BigDecimal minPrice , @RequestParam BigDecimal maxPrice){
        return ResponseEntity.ok(productService.countProductByPriceRange(minPrice, maxPrice));
    }
}
