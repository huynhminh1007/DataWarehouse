package com.example.datawarehouseserver.service.impl;

import com.example.datawarehouseserver.repository.DimDateRepository;
import com.example.datawarehouseserver.service.IManufacturerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ManufacturerService implements IManufacturerService {

    DimDateRepository dimDateRepository;
    DBConfigService dbConfigService;

    @Override
    public int insertFromStaging(String dbName, String table) {
        JdbcTemplate jdbcTemplate = dbConfigService.createJdbcTemplate(dbName);
        Integer currentDate = dimDateRepository.findByCurrentDate();

        String sql = MessageFormat.format("""
                INSERT INTO db_datawarehouse.dim_manufacturers (natural_key, manufacturer_name, is_active, insert_date, update_date)
                SELECT DISTINCT sp.brand_id, sp.brand_name, 1, {0,number,#}, {0,number,#}
                FROM {1} AS sp
                WHERE NOT EXISTS (
                    SELECT 1 FROM db_datawarehouse.dim_manufacturers dm
                    WHERE dm.natural_key = sp.brand_id
                )
                """, currentDate, dbName + "." + table);

        return jdbcTemplate.update(sql);
    }
}
