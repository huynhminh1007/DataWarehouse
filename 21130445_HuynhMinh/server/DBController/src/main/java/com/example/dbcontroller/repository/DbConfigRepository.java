package com.example.dbcontroller.repository;

import com.example.dbcontroller.entity.DBConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DbConfigRepository extends JpaRepository<DBConfig, Integer> {

    Optional<DBConfig> findByDbName(String dbName);
}
