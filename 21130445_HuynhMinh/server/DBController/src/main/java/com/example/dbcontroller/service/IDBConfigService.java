package com.example.dbcontroller.service;

import com.example.dbcontroller.entity.DBConfig;

import java.util.List;

public interface IDBConfigService {

    DBConfig getDbConfig(String dbName);
    List<DBConfig> getAllConfigs();
}
