package com.example.datawarehouseserver.service;

import com.example.datawarehouseserver.entity.Config;

import java.util.Optional;

public interface IConfigService {

    long count();

    Optional<Config> findLastByTable(String table);

    Config findRE(String table);
}
