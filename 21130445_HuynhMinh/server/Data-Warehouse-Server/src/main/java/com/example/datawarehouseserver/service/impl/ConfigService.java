package com.example.datawarehouseserver.service.impl;

import com.example.datawarehouseserver.entity.Config;
import com.example.datawarehouseserver.repository.ConfigRepository;
import com.example.datawarehouseserver.service.IConfigService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConfigService implements IConfigService {

    ConfigRepository configRepository;

    @Override
    public long count() {
        return configRepository.count();
    }

    @Override
    public Optional<Config> findLastByTable(String table) {
        return configRepository.findLastByTable(table);
    }

    @Override
    public Config findRE(String table) {
        return configRepository.findRE(table)
                .orElse(null);
    }
}
