package com.serhat.product.service;

import com.serhat.product.dto.object.PriceHistoryDTO;
import com.serhat.product.dto.object.ProductDto;
import com.serhat.product.dto.request.AddProductRequest;
import com.serhat.product.dto.request.DeleteProductRequest;
import com.serhat.product.dto.request.UpdatePriceRequest;
import com.serhat.product.dto.request.UpdateQuantityRequest;
import com.serhat.product.dto.response.AddProductResponse;
import com.serhat.product.dto.response.DeleteProductResponse;
import com.serhat.product.dto.response.UpdatePriceResponse;
import com.serhat.product.dto.response.UpdateQuantityResponse;
import com.serhat.product.entity.Category;
import com.serhat.product.entity.PriceHistory;
import com.serhat.product.entity.Product;
import com.serhat.product.exception.IllegalPriceRangeException;
import com.serhat.product.exception.ProductExistsException;
import com.serhat.product.exception.ProductNotFoundException;
import com.serhat.product.exception.SameQuantityRequestException;
import com.serhat.product.repository.PriceHistoryRepository;
import com.serhat.product.repository.ProductRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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


    @Transactional
    public DeleteProductResponse deleteProduct (Principal principal , String productCode){
        String name = principal.getName();
        Product product = productRepository.findProductByProductCode(productCode)
                .orElseThrow(()-> new ProductNotFoundException("Product not found by Product code : "+productCode));

        productRepository.delete(product);
        log.info("Product Deleted by"+name);
        return new DeleteProductResponse(
                "Product Deleted Successfully",
                product.getProductCode()
        );
    }


    public ProductDto productInfo(String productCode){
        Product product = productRepository.findProductByProductCode(productCode)
                .orElseThrow(()-> new ProductNotFoundException("Product not found "+productCode));

      return new ProductDto(
              product.getName(),
              product.getProductCode(),
              product.getYear(),
              product.getPrice(),
              product.getDescription(),
              product.getQuantity(),
              product.getCategory(),
              product.getCountryOfOrigin()
      );
    }


    @Transactional
    public UpdatePriceResponse updatePrice (UpdatePriceRequest request){
        Product product = productRepository.findProductByProductCode(request.productCode())
                .orElseThrow(()-> new ProductNotFoundException("Product Not Found : "+request.productCode()));

        BigDecimal currentPrice = product.getPrice();
        BigDecimal requestedPrice = request.updatedPrice();
        PriceHistory priceHistory = PriceHistory.builder()
                .product(product)
                .original_price(currentPrice)
                .updated_price(requestedPrice)
                .validFrom(LocalDateTime.now())
                .validTo(LocalDateTime.now().plusWeeks(1))
                .updatedAt(LocalDateTime.now())
                .build();

        String difference = priceHistory.calculatePriceDifference(currentPrice,requestedPrice);
        product.setPrice(requestedPrice);
        priceHistory.setPriceDifference(difference);
        priceHistoryRepository.save(priceHistory);
        productRepository.save(product);

        return new UpdatePriceResponse(
                "Price Updated Successfully",
                difference
        );
    }

    @Transactional
    public UpdateQuantityResponse updateQuantity(UpdateQuantityRequest request){
        Product product = productRepository.findProductByProductCode(request.productCode())
                .orElseThrow(()-> new ProductNotFoundException("Product Not Found"));

        int currentQuantity = product.getQuantity();
        int requestedQuantity = request.newQuantity();

        if(currentQuantity == requestedQuantity){
            throw new SameQuantityRequestException("Current quantity already same as requested quantity");
        }
        if(requestedQuantity<0){
            throw new IllegalPriceRangeException("quantity cannot be negative");
        }
        product.setQuantity(requestedQuantity);
        log.info("Product quantity , with product code : "+product.getProductCode() + " updated ");
        productRepository.save(product);

        return new UpdateQuantityResponse(
                "Quantity Updated",
                product.getProductCode(),
                product.getQuantity()
        );
    }


    public List<ProductDto> listProductByCategory (Category category){
        List<Product> products = productRepository.findByCategory(category);
        if(products.isEmpty()){
            return Collections.emptyList();
        }
        return products.stream()
                .map(product -> new ProductDto(
                        product.getName(),
                        product.getProductCode(),
                        product.getYear(),
                        product.getPrice(),
                        product.getDescription(),
                        product.getQuantity(),
                        product.getCategory(),
                        product.getCountryOfOrigin()
                ))
                .toList();
    }

    public List<ProductDto> listProductByPriceRange(@NotNull BigDecimal minPrice ,@NotNull BigDecimal maxPrice){
        if(minPrice.compareTo(maxPrice)>0){
            throw new IllegalPriceRangeException("Minimum price cannot be higher than maximum price");
        }
        List<Product> products = productRepository.findByPriceBetween(minPrice,maxPrice);
        if(products.isEmpty()){
           return Collections.emptyList();
        }

        return products.stream()
                .map(product -> new ProductDto(
                        product.getName(),
                        product.getProductCode(),
                        product.getYear(),
                        product.getPrice(),
                        product.getDescription(),
                        product.getQuantity(),
                        product.getCategory(),
                        product.getCountryOfOrigin()
                ))
                .toList();
    }

    public List<ProductDto> findProductByName(String productName){
        List<Product> products = productRepository.findProductByName(productName);
        if(products.isEmpty()){
            return Collections.emptyList();
        }
        return products.stream()
                .map(product -> new ProductDto(
                        product.getName(),
                        product.getProductCode(),
                        product.getYear(),
                        product.getPrice(),
                        product.getDescription(),
                        product.getQuantity(),
                        product.getCategory(),
                        product.getCountryOfOrigin()
                ))
                .toList();
    }

    public long countProductByPriceRange(@NotNull BigDecimal minPrice, @NotNull BigDecimal maxPrice) {
        return productRepository.countByPriceBetween(minPrice, maxPrice);
    }

    public long countProductByCategory(Category category) {
        return productRepository.countByCategory(category);
    }

    public List<PriceHistoryDTO> priceHistoryForProduct(String productCode){
        Product product = productRepository.findProductByProductCode(productCode)
                .orElseThrow(()-> new ProductNotFoundException("Product Not Found!"));

        List<PriceHistory> priceHistories = priceHistoryRepository.findByProduct(product);
        if(priceHistories.isEmpty()){
            return Collections.emptyList();
        }

        return priceHistories.stream()
                .map(priceHistory -> new PriceHistoryDTO(
                        priceHistory.getProduct().getProductCode(),
                        priceHistory.getOriginal_price(),
                        priceHistory.getUpdated_price(),
                        priceHistory.getPriceDifference(),
                        priceHistory.getValidFrom(),
                        priceHistory.getValidTo()
                ))
                .toList();


    }


}
