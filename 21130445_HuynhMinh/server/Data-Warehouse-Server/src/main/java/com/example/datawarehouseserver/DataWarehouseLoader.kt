package com.example.datawarehouseserver

import com.example.datawarehouseserver.entity.Log
import com.example.datawarehouseserver.service.impl.ConfigService
import com.example.datawarehouseserver.service.impl.LogService
import com.example.datawarehouseserver.service.impl.ManufacturerService
import com.example.datawarehouseserver.service.impl.ProductService
import kotlinx.coroutines.*
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class DataWarehouseLoader(
    private val manufacturerService: ManufacturerService,
    private val configService: ConfigService,
    private val logService: LogService,
    private val productService: ProductService
) : CommandLineRunner {

    companion object {
        const val DIM_PRODUCT_TABLE = "dim_products"
        const val DIM_MANUFACTURER_TABLE = "dim_manufacturers"
    }

    override fun run(vararg args: String?) {
        CoroutineScope(Dispatchers.IO).launch {
            launch { insertManufacturerFromStaging() }
            launch { insertProductFromStaging() }
        }
    }

    suspend fun insertManufacturerFromStaging() {
        withTimeout(86400000) { // 24 giờ
            while (isActive) {
                var config = configService.findRE(DIM_MANUFACTURER_TABLE)

                if (config == null) {
                    config = configService.findLastByTable(DIM_MANUFACTURER_TABLE).getOrNull()

                    if (config == null) {
                        saveLog(null, "load_to_warehouse_failed", "Config is missing", "warn")
                        delay(60 * 1000)
                        continue
                    }
                }

                val log = config.log.first()
                val dbName = config.stagingConfig.dbName
                val msg = "$dbName.${config.stagingTable} to db_datawarehouse.${config.datawarehouseTable}"

                if (log.status == "loading_to_warehouse") {
                    saveLog(
                        log.clone().apply { id = null },
                        "load_to_warehouse_failed",
                        "Insert from $msg Failed. A process is already running.",
                        "warn"
                    )
                    delay(60 * 1000)
                    continue
                } else if (log.status != "RE") {
                    saveLog(
                        log.clone().apply { id = null },
                        "load_to_warehouse_failed",
                        "Insert from $msg Failed. RE is missing",
                        "warn"
                    )
                    delay(60 * 1000)
                    continue
                }

                // Gọi insert mà không cần đợi nó hoàn thành trước khi tiếp tục vòng lặp
                launch {
                    try {
                        saveLog(
                            log,
                            "loading_to_warehouse",
                            "Loading from $msg",
                            "info"
                        )
                        delay(1000)
//                        delay(120 * 1000)
                        val countInserted = manufacturerService.insertFromStaging(dbName, config.stagingTable)

                        saveLog(
                            log,
                            "load_to_warehouse_completed",
                            "Inserted $countInserted records from $msg",
                            "info"
                        )
                    } catch (e: Exception) {
                        saveLog(
                            log.clone().apply { id = null },
                            "Insert Failed from $msg",
                            "load_to_warehouse_failed",
                            "warn"
                        )
                        println("Lỗi khi insert: ${e.message}")
                    }
                }

                // Delay trước khi tiếp tục vòng lặp
//                delay(config.period * 1000)
                delay(config.period * 1000)
            }
        }
    }

    suspend fun insertProductFromStaging() {
        delay(2000)
        withTimeout(86400000) { // 24 giờ
            while (isActive) {
                var config = configService.findRE(DIM_PRODUCT_TABLE)

                if (config == null) {
                    config = configService.findLastByTable(DIM_PRODUCT_TABLE).getOrNull()

                    if (config == null) {
                        saveLog(null, "load_to_warehouse_failed", "Config is missing", "warn")
                        delay(60 * 1000)
                        continue
                    }
                }

                val log = config.log.first()
                val dbName = config.stagingConfig.dbName
                val msg = "$dbName.${config.stagingTable} to db_datawarehouse.${config.datawarehouseTable}"

                if (log.status == "loading_to_warehouse") {
                    saveLog(
                        log.clone().apply { id = null },
                        "load_to_warehouse_failed",
                        "Insert from $msg Failed. A process is already running.",
                        "warn"
                    )
                    delay(60 * 1000)
                    continue
                } else if (log.status != "RE") {
                    saveLog(
                        log.clone().apply { id = null },
                        "load_to_warehouse_failed",
                        "Insert from $msg Failed. RE is missing",
                        "warn"
                    )
                    delay(60 * 1000)
                    continue
                }

                // Gọi insert mà không cần đợi nó hoàn thành trước khi tiếp tục vòng lặp
                launch {
                    try {
                        saveLog(
                            log,
                            "loading_to_warehouse",
                            "Loading from $msg",
                            "info"
                        )
                        delay(1000)
//                        delay(120 * 1000)
                        val countInsertNew = productService.insertNewFromStaging(dbName, config.stagingTable)
                        val countUpdateExpire =
                            productService.updateExpireProductFromStaging(dbName, config.stagingTable)
                        val countInsertNewType2 = productService.insertNewProductType2(dbName, config.stagingTable)

                        saveLog(
                            log,
                            "load_to_warehouse_completed",
                            "Inserted $countInsertNew new records, Updated $countUpdateExpire expire records, Inserted $countInsertNewType2 records type 2 from $msg",
                            "info"
                        )
                    } catch (e: Exception) {
                        saveLog(
                            log.clone().apply { id = null },
                            "load_to_warehouse_failed",
                            "Insert Failed from $msg",
                            "warn"
                        )
                        println("Lỗi khi insert: ${e.message}")
                    }
                }

                // Delay trước khi tiếp tục vòng lặp
//                delay(config.period * 1000)
                delay(config.period * 1000)
            }
        }
    }

    fun saveLog(log: Log?, status: String, msg: String, level: String): Log {
        return logService.save((log ?: Log()).apply {
            this.status = status
            this.message = msg
            this.level = level
        })
    }
}
