package com.example.datawarehouseserver.service.impl;

import com.example.datawarehouseserver.repository.DimDateRepository;
import com.example.datawarehouseserver.service.IProductService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class ProductService implements IProductService {

    DBConfigService dbConfigService;
    DimDateRepository dimDateRepository;

    @Transactional
    @Override
    public int insertNewFromStaging(String dbName, String table) {
        JdbcTemplate jdbcTemplate = dbConfigService.createJdbcTemplate(dbName);
        Integer currentDate = dimDateRepository.findByCurrentDate();

        String sql = MessageFormat.format("""
                INSERT INTO db_datawarehouse.dim_products (natural_key, sku_no, product_name, product_description, image_url, specifications,
                     price, original_price, stock, manufacturer_id, is_active, insert_date, update_date)
                SELECT sp.natural_key, sp.sku, sp.product_name, sp.short_description, sp.thumbnail_url, sp.specifications, sp.price, sp.original_price,
                     sp.stock_item_qty, dm.id, 1, {0, number, #}, {0, number, #}
                FROM {1} AS sp
                JOIN db_datawarehouse.dim_manufacturers AS dm ON dm.manufacturer_name = sp.brand_name
                WHERE NOT EXISTS (
                    SELECT 1 FROM db_datawarehouse.dim_products AS dp
                    WHERE dp.natural_key = sp.natural_key
                )
                """, currentDate, dbName + "." + table);

        return jdbcTemplate.update(sql);
    }

    @Transactional
    @Override
    public int updateExpireProductFromStaging(String dbName, String table) {
        JdbcTemplate jdbcTemplate = dbConfigService.createJdbcTemplate(dbName);
        Integer currentDate = dimDateRepository.findByCurrentDate();

        jdbcTemplate.execute("SET SQL_SAFE_UPDATES = 0");

        Date curDate = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = dateFormat.format(curDate);

        String sql = MessageFormat.format("""
                UPDATE db_datawarehouse.dim_products AS dp
                SET dp.is_active = 0, dp.expired_date = \"{2}\", dp.update_date = {0, number, #}
                WHERE dp.expired_date = \"9999-12-31\"
                AND dp.is_active = 1
                AND EXISTS (
                    SELECT 1 FROM {1} AS sp
                    WHERE dp.natural_key = sp.natural_key
                    AND (dp.sku_no <> sp.sku
                        OR dp.product_name <> sp.product_name
                        OR dp.product_description <> sp.short_description
                        OR dp.image_url <> sp.thumbnail_url
                        OR dp.specifications <> sp.specifications
                        OR dp.price <> sp.price
                        OR dp.original_price <> sp.original_price
                        OR dp.stock <> sp.stock_item_qty)
                )
                """, currentDate, dbName + "." + table, formattedDate);

        int rowsAffected = jdbcTemplate.update(sql);

        jdbcTemplate.execute("SET SQL_SAFE_UPDATES = 1");

        return rowsAffected;
    }

    @Transactional
    @Override
    public int insertNewProductType2(String dbName, String table) {
        JdbcTemplate jdbcTemplate = dbConfigService.createJdbcTemplate(dbName);
        Integer currentDate = dimDateRepository.findByCurrentDate();

        String sql = MessageFormat.format("""
                INSERT INTO db_datawarehouse.dim_products (natural_key, sku_no, product_name, product_description, image_url, specifications,
                    price, original_price, stock, manufacturer_id, is_active, insert_date, update_date, expired_date)
                SELECT sp.natural_key, sp.sku, sp.product_name, sp.short_description, sp.thumbnail_url, sp.specifications, sp.price, sp.original_price,
                    sp.stock_item_qty, dm.id, 1, {0, number, #}, {0, number, #}, \"9999-12-31\"
                FROM {1} AS sp
                JOIN db_datawarehouse.dim_manufacturers AS dm ON dm.manufacturer_name = sp.brand_name
                WHERE EXISTS (
                    SELECT 1 FROM db_datawarehouse.dim_products AS dp
                    WHERE dp.natural_key = sp.natural_key
                    AND dp.is_active = 0
                    AND (dp.sku_no <> sp.sku
                        OR dp.product_name <> sp.product_name
                        OR dp.product_description <> sp.short_description
                        OR dp.image_url <> sp.thumbnail_url
                        OR dp.specifications <> sp.specifications
                        OR dp.price <> sp.price
                        OR dp.original_price <> sp.original_price
                        OR dp.stock <> sp.stock_item_qty)
                )
                """, currentDate, dbName + "." + table);

        return jdbcTemplate.update(sql);
    }
}
