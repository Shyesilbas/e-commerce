package com.serhat.product.repository;

import com.serhat.product.entity.Category;
import com.serhat.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {
    Optional<Product> findProductByProductCode(String productCode);
    List<Product> findByCategory(Category category);
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    long countByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    long countByCategory(Category category);
}
