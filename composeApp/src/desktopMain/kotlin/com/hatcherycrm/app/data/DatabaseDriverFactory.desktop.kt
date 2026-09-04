package com.hatcherycrm.app.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.hatcherycrm.app.db.HatcheryDatabase
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val dbFile = File(System.getProperty("user.home"), ".hatcherycrm/hatchery.db")
        dbFile.parentFile?.mkdirs()
        val isNewDatabase = !dbFile.exists()
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")
        if (isNewDatabase) {
            HatcheryDatabase.Schema.create(driver)
        }
        return driver
    }
}
