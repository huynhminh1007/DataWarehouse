USE db_staging;

DELIMITER //

-- Insert dim_dates vào data warehouse
DROP PROCEDURE IF EXISTS insert_dim_dates;
CREATE PROCEDURE insert_dim_dates()
BEGIN
    INSERT INTO db_datawarehouse.dim_dates (
        date_sk,
        full_date,
        day_since_2005,
        month_since_2005,
        day_of_week,
        calendar_month,
        calendar_year,
        calendar_year_month,
        day_of_month,
        day_of_year,
        week_of_year_sunday,
        year_week_sunday,
        week_sunday_start,
        week_of_year_monday,
        year_week_monday,
        week_monday_start,
        quarter_of_year,
        quarter_since_2005,
        holiday,
        date_type
    )
    SELECT 
        date_sk,
        full_date,
        day_since_2005,
        month_since_2005,
        day_of_week,
        calendar_month,
        calendar_year,
        calendar_year_month,
        day_of_month,
        day_of_year,
        week_of_year_sunday,
        year_week_sunday,
        week_sunday_start,
        week_of_year_monday,
        year_week_monday,
        week_monday_start,
        quarter_of_year,
        quarter_since_2005,
        holiday,
        date_type
    FROM db_staging.dim_dates AS ds
    WHERE NOT EXISTS (
        SELECT 1 
        FROM db_datawarehouse.dim_dates AS dw
        WHERE dw.date_sk = ds.date_sk
    );
END //

-- Transform data staging
DROP PROCEDURE IF EXISTS transform_data_staging;
CREATE PROCEDURE transform_data_staging()
BEGIN
	DROP TEMPORARY TABLE IF EXISTS temp_staging_products;
    CREATE TEMPORARY TABLE temp_staging_products (
        natural_key INT UNSIGNED NOT NULL,
        sku_no VARCHAR(32) NOT NULL,
        product_name VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
        product_description VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
        image_url VARCHAR(255),
        specifications JSON DEFAULT NULL,
        manufacturer_id INT UNSIGNED,
        manufacturer_name VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
        price DECIMAL(10, 2) NOT NULL,
        original_price DECIMAL(10, 2) NOT NULL,
        stock INT UNSIGNED DEFAULT 0
    );

    INSERT INTO temp_staging_products 
	SELECT filtered.natural_key, filtered.sku_no, filtered.product_name, filtered.product_description, filtered.image_url, 
       filtered.specifications, filtered.manufacturer_id, filtered.manufacturer_name,
       filtered.price, filtered.original_price, filtered.stock
	FROM (
		SELECT p.natural_key, p.sku AS sku_no, p.product_name, p.short_description AS product_description, 
           p.thumbnail_url AS image_url, 
			CASE 
				WHEN JSON_VALID(REPLACE(p.specifications, "'", '"')) THEN JSON_UNQUOTE(REPLACE(p.specifications, "'", '"'))
				ELSE NULL
			END AS specifications
           , p.brand_id AS manufacturer_id, 
           p.brand_name AS manufacturer_name, p.price, p.original_price, p.stock_item_qty AS stock, 
           ROW_NUMBER() OVER (
               PARTITION BY p.natural_key, p.sku, p.product_name, p.short_description, 
                            p.thumbnail_url, p.specifications, p.brand_id, p.brand_name, 
                            p.price, p.original_price, p.stock_item_qty
               ORDER BY p.natural_key
           ) AS row_num
		FROM db_staging.dim_tiki_products AS p
		WHERE p.brand_id IS NOT NULL
			AND p.brand_name NOT LIKE 'None'
			AND p.natural_key IS NOT NULL
			AND p.sku IS NOT NULL
			AND p.product_name NOT LIKE 'None'
			AND p.price >= 0
			AND p.original_price >= 0
			AND p.stock_item_qty >= 0
	) AS filtered
	WHERE filtered.row_num = 1;

    CREATE INDEX idx_priority ON temp_staging_products (natural_key, sku_no, manufacturer_id);
    
END //

-- Add data source function
DROP FUNCTION IF EXISTS insert_data_source;
CREATE FUNCTION insert_data_source(source_name VARCHAR(50))
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE source_id INT;
    
    SELECT id INTO source_id
    FROM db_datawarehouse.data_sources AS ds
    WHERE ds.source_name LIKE source_name
    LIMIT 1;
    
    IF source_id IS NULL THEN
        INSERT INTO db_datawarehouse.data_sources (source_name) VALUES (source_name);
        SELECT LAST_INSERT_ID() INTO source_id;
    END IF;
    
    RETURN source_id;
END //

-- Load data from staging to data warehouse
-- DROP PROCEDURE IF EXISTS load_data_staging_to_datawarehouse;

DELIMITER $$

CREATE PROCEDURE load_data_staging_to_datawarehouse()
BEGIN
    DECLARE source_id INT;
	DECLARE cur_date INT;
    
    -- Bắt đầu giao dịch
    START TRANSACTION;
    
	CALL insert_dim_dates();
    
    -- Chèn dữ liệu nguồn 'tiki' vào data warehouse
    SET source_id = insert_data_source('tiki');
	SET cur_date = (
		SELECT date_sk
		FROM dim_dates
		WHERE full_date = CURDATE()
		LIMIT 1
	);
    -- Gọi thủ tục để biến đổi dữ liệu từ staging
    CALL transform_data_staging();

    -- Chèn dữ liệu vào dim_manufacturers
    INSERT IGNORE INTO db_datawarehouse.dim_manufacturers(natural_key, manufacturer_name, source_id, is_active, insert_date)
    SELECT DISTINCT tp.manufacturer_id, tp.manufacturer_name, source_id, 1, cur_date
    FROM temp_staging_products AS tp
    WHERE NOT EXISTS (
        SELECT 1
        FROM db_datawarehouse.dim_manufacturers dm
        WHERE dm.source_id = source_id
        AND dm.natural_key = tp.manufacturer_id
    );

    -- Tạo bảng tạm cho các sản phẩm mới có matching natural_key
    DROP TEMPORARY TABLE IF EXISTS temp_dw_products;
    CREATE TEMPORARY TABLE temp_dw_products AS
    SELECT p.natural_key, p.sku_no, p.product_name, p.product_description, p.image_url, p.specifications,
           dm.natural_key AS manufacturer_id, p.price, p.original_price, p.stock
    FROM db_datawarehouse.dim_products AS p
    JOIN temp_staging_products AS tsp ON p.natural_key = tsp.natural_key
    JOIN db_datawarehouse.dim_manufacturers AS dm ON p.manufacturer_id = dm.id
    WHERE p.is_active = 1 AND p.source_id = source_id;

    CREATE INDEX idx_priority ON temp_dw_products (natural_key, sku_no);

    -- Thêm sản phẩm mới vào data warehouse (sản phẩm có trong staging nhưng chưa có trong data warehouse)
    INSERT INTO db_datawarehouse.dim_products (natural_key, sku_no, product_name, product_description, image_url,
                                               specifications, manufacturer_id, insert_date, is_active, source_id,
                                               price, original_price, stock, update_date)
    SELECT tsp.natural_key, tsp.sku_no, tsp.product_name, tsp.product_description, tsp.image_url, tsp.specifications,
           dm.id, cur_date, 1, source_id, tsp.price, tsp.original_price, tsp.stock, cur_date
    FROM temp_staging_products AS tsp
    JOIN db_datawarehouse.dim_manufacturers AS dm ON dm.manufacturer_name = tsp.manufacturer_name
    WHERE NOT EXISTS (
        SELECT 1 FROM temp_dw_products AS tdp
        WHERE tdp.natural_key = tsp.natural_key
    );

    -- Tạo bảng tạm cho các sản phẩm cần cập nhật
    DROP TEMPORARY TABLE IF EXISTS temp_update_products;
    CREATE TEMPORARY TABLE temp_update_products AS
    SELECT tsp.natural_key, tsp.sku_no, tsp.product_name, tsp.product_description, tsp.image_url, tsp.specifications,
           tsp.price, tsp.original_price, tsp.stock, tsp.manufacturer_name
    FROM temp_staging_products AS tsp
    WHERE EXISTS (
        SELECT 1 FROM temp_dw_products AS tdp
        WHERE tdp.natural_key = tsp.natural_key
        AND (tdp.sku_no <> tsp.sku_no
             OR tdp.product_name <> tsp.product_name
             OR tdp.product_description <> tsp.product_description
             OR tdp.image_url <> tsp.image_url
             OR tdp.specifications <> tsp.specifications
             OR tdp.price <> tsp.price
             OR tdp.original_price <> tsp.original_price
             OR tdp.stock <> tsp.stock)
    );

    -- Cập nhật các sản phẩm cũ, set is_active = 0
    SET SQL_SAFE_UPDATES = 0;

	UPDATE db_datawarehouse.dim_products AS dp
	SET dp.is_active = 0,
		dp.expired_date = CURRENT_DATE,
        dp.update_date = cur_date
	WHERE EXISTS (
		SELECT 1 FROM temp_update_products AS tup
		WHERE tup.natural_key = dp.natural_key
	);

	SET SQL_SAFE_UPDATES = 1;

    -- Chèn sản phẩm đã cập nhật vào data warehouse (type 2)
    INSERT INTO db_datawarehouse.dim_products (natural_key, sku_no, product_name, product_description, image_url,
                                               specifications, manufacturer_id, insert_date, is_active, source_id,
                                               price, original_price, stock, update_date)
    SELECT tup.natural_key, tup.sku_no, tup.product_name, tup.product_description, tup.image_url, tup.specifications,
           dm.id, cur_date, 1, source_id, tup.price, tup.original_price, tup.stock, cur_date
    FROM temp_update_products AS tup
    JOIN db_datawarehouse.dim_manufacturers AS dm ON dm.manufacturer_name = tup.manufacturer_name;

    -- Xóa bảng tạm sau khi hoàn tất
    DROP TEMPORARY TABLE IF EXISTS temp_staging_products;
    DROP TEMPORARY TABLE IF EXISTS temp_dw_products;
    DROP TEMPORARY TABLE IF EXISTS temp_update_products;

    -- Cam kết giao dịch
    COMMIT;
END$$

DELIMITER ;

CALL load_data_staging_to_datawarehouse();
DROP PROCEDURE load_data_staging_to_datawarehouse;