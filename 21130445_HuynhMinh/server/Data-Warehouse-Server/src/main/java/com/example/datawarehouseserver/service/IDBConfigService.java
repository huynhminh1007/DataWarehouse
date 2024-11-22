package com.example.datawarehouseserver.service;

import com.example.datawarehouseserver.entity.DBConfig;

import java.util.List;

public interface IDBConfigService {

    DBConfig getDbConfig(String dbName);

    List<DBConfig> getAllConfigs();
}
