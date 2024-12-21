package com.serhat.product.repository;

import com.serhat.product.entity.PriceHistory;
import com.serhat.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PriceHistoryRepository extends JpaRepository<PriceHistory,Integer> {
    List<PriceHistory> findByProduct(Product product);
}
