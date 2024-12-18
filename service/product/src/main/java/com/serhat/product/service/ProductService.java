package com.serhat.product.service;

import com.serhat.product.dto.request.AddProductRequest;
import com.serhat.product.dto.response.AddProductResponse;
import com.serhat.product.entity.Product;
import com.serhat.product.exception.ProductExistsException;
import com.serhat.product.repository.PriceHistoryRepository;
import com.serhat.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final PriceHistoryRepository priceHistoryRepository;


    @Transactional
    public AddProductResponse addProduct (Principal principal , AddProductRequest request){
        String name = principal.getName();
        log.info("Admin : "+name + " adding a product with Product code : "+request.productDto().productCode());

        boolean isProductExists = productRepository.findProductByProductCode(request.productDto().productCode()).isPresent();
        if(isProductExists){
            throw new ProductExistsException("Product Already Exists by the Product code : "+request.productDto().productCode());
        }

        Product product = Product.builder()
                .name(request.productDto().name())
                .productCode(request.productDto().productCode())
                .price(request.productDto().price())
                .year(request.productDto().year())
                .quantity(request.productDto().quantity())
                .description(request.productDto().description())
                .category(request.productDto().category())
                .countryOfOrigin(request.productDto().countryOfOrigin())
                .build();

        productRepository.save(product);

        return new AddProductResponse(
                "Product Added successfully",
                product.getProductCode()
        );


    }

}
