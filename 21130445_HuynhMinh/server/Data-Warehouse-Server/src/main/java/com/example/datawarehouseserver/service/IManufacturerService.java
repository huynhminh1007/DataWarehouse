package com.example.datawarehouseserver.service;

public interface IManufacturerService {
    int insertFromStaging(String dbName, String table);
}
