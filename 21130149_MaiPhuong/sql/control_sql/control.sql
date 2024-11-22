CREATE DATABASE IF NOT EXISTS db_controller;
USE db_controller;

ALTER DATABASE db_controller CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS db_configs (
	id INT AUTO_INCREMENT PRIMARY KEY,
    db_name VARCHAR(100),
    url VARCHAR(255),
    username VARCHAR(100),
    password VARCHAR(100),
    driver_class_name VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS configs (
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	  file_name VARCHAR(255),
    source_path VARCHAR(255),
		file_location VARCHAR(255),
    backup_path VARCHAR(255),
    staging_config INT,
    datawarehouse_config INT,
    staging_table VARCHAR(50),
    datawarehouse_table VARCHAR(50),
    period Long,
    version VARCHAR(50),
    is_active TINYINT(1) UNSIGNED DEFAULT '0' COMMENT '0: inactive, 1: active',
    insert_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (staging_config) REFERENCES db_configs(id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (datawarehouse_config) REFERENCES db_configs(id) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS logs (
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    config_id INT UNSIGNED,
    status VARCHAR(100) COMMENT 'loading_to_staging, load_to_staging_completed, load_to_staging_failed, loading_to_warehouse, load_to_warehouse_completed, load_to_warehouse_failed',
		file_name VARCHAR(255),
		file_size BIGINT,
    message TEXT,
    begin_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    level VARCHAR(100) COMMENT 'info, warn, error, debug',
    
	FOREIGN KEY (config_id) REFERENCES configs(id) ON DELETE SET NULL ON UPDATE CASCADE
);

