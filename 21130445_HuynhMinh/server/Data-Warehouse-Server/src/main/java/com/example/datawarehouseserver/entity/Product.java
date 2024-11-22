package com.example.datawarehouseserver.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "dim_products")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    Integer naturalKey;
    String skuNo;
    String productName;
    String productDescription;
    String imageUrl;

    @Column(name = "specifications", columnDefinition = "JSON")
    String specifications;

    @Column(name = "price")
    BigDecimal price;

    @Column(name = "original_price")
    BigDecimal originalPrice;

    Integer stock;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manufacturer_id")
    Manufacturer manufacturer;

    @Column(name = "expired_date", columnDefinition = "DATE DEFAULT '9999-12-31'")
    Date expiredDate;
}