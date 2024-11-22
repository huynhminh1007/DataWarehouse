package com.example.datawarehouseserver.repository;

import com.example.datawarehouseserver.entity.DBConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DbConfigRepository extends JpaRepository<DBConfig, Integer> {

    Optional<DBConfig> findByDbName(String dbName);
}
