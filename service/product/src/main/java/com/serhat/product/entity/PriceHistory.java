package com.serhat.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "price_history")
public class PriceHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer phId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "original_price", nullable = false)
    private BigDecimal original_price;

    @Column(name = "updated", nullable = false)
    private BigDecimal updated_price;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "price_difference", nullable = false)
    private String priceDifference = calculatePriceDifference(original_price,updated_price);

    public String calculatePriceDifference(BigDecimal oldPrice, BigDecimal newPrice) {
        if (oldPrice != null && newPrice != null && oldPrice.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal difference = newPrice.subtract(oldPrice);
            BigDecimal percentageDifference = difference.divide(oldPrice, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

            this.priceDifference = (percentageDifference.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "")
                    + percentageDifference.setScale(2, RoundingMode.HALF_UP)
                    + "%";
        }
        return priceDifference;
    }


}
