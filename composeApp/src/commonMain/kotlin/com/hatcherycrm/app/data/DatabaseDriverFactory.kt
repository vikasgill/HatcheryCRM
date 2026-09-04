package com.hatcherycrm.app.data

import app.cash.sqldelight.db.SqlDriver

/**
 * Each platform provides its own SQLDelight driver implementation
 * (Android: AndroidSqliteDriver, iOS: NativeSqliteDriver, Desktop: JdbcSqliteDriver).
 */
expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
