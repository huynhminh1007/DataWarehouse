package com.example.datawarehouseserver.service;

public interface IProductService {
    int insertNewFromStaging(String dbName, String table);
    int updateExpireProductFromStaging(String dbName, String table);
    int insertNewProductType2(String dbName, String table);
}
