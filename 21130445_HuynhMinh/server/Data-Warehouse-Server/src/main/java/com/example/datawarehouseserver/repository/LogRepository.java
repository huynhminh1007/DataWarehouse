package com.example.datawarehouseserver.repository;

import com.example.datawarehouseserver.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Integer> {

    @Modifying
    @Query("UPDATE Log l SET l.status = :status WHERE l.id = :id")
    int updateStatusById(Integer id, String status);
}