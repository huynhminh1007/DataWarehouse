package com.example.datawarehouseserver.service.impl;

import com.example.datawarehouseserver.entity.DBConfig;
import com.example.datawarehouseserver.repository.DbConfigRepository;
import com.example.datawarehouseserver.service.IDBConfigService;
import com.zaxxer.hikari.HikariDataSource;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class DBConfigService implements IDBConfigService {

    DbConfigRepository dbConfigRepository;

    @Override
    public DBConfig getDbConfig(String dbName) {
        return dbConfigRepository.findByDbName(dbName)
                .orElseThrow(() -> new RuntimeException("Database configuration not found for: " + dbName));
    }

    @Override
    public List<DBConfig> getAllConfigs() {
        return dbConfigRepository.findAll();
    }

    public DataSource createDataSource(String dbName) {
        DBConfig dbConfig = getDbConfig(dbName);
        return createDataSource(dbConfig);
    }

    public DataSource createDataSource(DBConfig dbConfig) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(dbConfig.getUrl());
        dataSource.setUsername(dbConfig.getUsername());
        dataSource.setPassword(dbConfig.getPassword());
        dataSource.setDriverClassName(dbConfig.getDriverClassName());

        return dataSource;
    }


    public JdbcTemplate createJdbcTemplate(String dbName) {
        return new JdbcTemplate(createDataSource(dbName));
    }

    public JdbcTemplate createJdbcTemplate(DBConfig dbConfig) {
        return new JdbcTemplate(createDataSource(dbConfig));
    }
}
