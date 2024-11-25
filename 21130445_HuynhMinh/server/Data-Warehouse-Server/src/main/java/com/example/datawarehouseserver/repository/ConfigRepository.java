package com.example.datawarehouseserver.repository;

import com.example.datawarehouseserver.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigRepository extends JpaRepository<Config, Integer> {

    @Query("""
            SELECT c FROM Config c
            JOIN FETCH c.stagingConfig dbStaging
            JOIN FETCH c.datawarehouseConfig dbWarehouse
            JOIN FETCH c.log l
            WHERE c.datawarehouseTable LIKE :table
                AND c.isActive = true
            ORDER BY l.beginDate DESC
            LIMIT 1
            """
    )
    Optional<Config> findLastByTable(String table);

    @Query("""
            SELECT c FROM Config c
            JOIN FETCH c.stagingConfig dbStaging
            JOIN FETCH c.datawarehouseConfig dbWarehouse
            JOIN FETCH c.log l
            WHERE l.status = 'RE'
                AND c.datawarehouseTable LIKE :table
                AND c.isActive = true
            ORDER BY l.beginDate DESC
            LIMIT 1
            """
    )
    Optional<Config> findRE(String table);
}
