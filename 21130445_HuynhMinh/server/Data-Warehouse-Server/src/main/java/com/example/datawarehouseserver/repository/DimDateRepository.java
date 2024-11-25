package com.example.datawarehouseserver.repository;

import com.example.datawarehouseserver.entity.datawarehouse.DimDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DimDateRepository extends JpaRepository<DimDate, Integer> {

    @Query("SELECT d.dateSk FROM DimDate d WHERE d.fullDate = CURRENT_DATE")
    Integer findByCurrentDate();
}