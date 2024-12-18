package com.serhat.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;

    @Column(name = "product_name",nullable = false)
    private String name;

    @Column(name = "product_code",nullable = false,unique = true)
    private String productCode;

    @Column(name = "year",nullable = false)
    private int year;

    @Column(name = "price",nullable = false)
    private BigDecimal price;

    @Column(name = "description",nullable = false)
    private String description;

    @Column(name = "quantity",nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "category",nullable = false)
    private Category category;

    @Column(name = "country_of_origin", nullable = false)
    private String countryOfOrigin;



}
