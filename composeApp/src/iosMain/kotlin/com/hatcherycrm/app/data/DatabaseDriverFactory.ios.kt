package com.hatcherycrm.app.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.hatcherycrm.app.db.HatcheryDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(HatcheryDatabase.Schema, "hatchery.db")
    }
}
