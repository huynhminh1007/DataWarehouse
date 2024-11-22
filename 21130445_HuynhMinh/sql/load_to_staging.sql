USE db_staging;

TRUNCATE TABLE dim_tiki_products;

LOAD DATA INFILE '/var/lib/mysql-files/crawled_data_laptop.csv'
INTO TABLE dim_tiki_products
FIELDS TERMINATED BY ',' 
OPTIONALLY ENCLOSED BY '"' 
LINES TERMINATED BY '\n' 
IGNORE 1 ROWS;

SELECT COUNT(*) FROM dim_tiki_products;
SELECT * FROM dim_tiki_products;