package com.example.datawarehouseserver.repository;

import com.example.datawarehouseserver.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
