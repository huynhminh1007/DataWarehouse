package com.example.datawarehouseserver.entity.datawarehouse;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class BaseDim {
    @Column(name = "is_active", columnDefinition = "TINYINT(1) DEFAULT 0")
    Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "delete_date")
    DimDate deleteDate;

    @ManyToOne
    @JoinColumn(name = "insert_date")
    DimDate insertDate;

    @ManyToOne
    @JoinColumn(name = "update_date")
    DimDate updateDate;
}
