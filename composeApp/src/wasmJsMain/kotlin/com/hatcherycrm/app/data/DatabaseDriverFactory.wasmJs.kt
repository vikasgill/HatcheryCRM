package com.hatcherycrm.app.data

import app.cash.sqldelight.db.SqlDriver

/**
 * The web (wasmJs) build does not use SQLDelight persistence — it relies on the
 * in-memory repositories instead. This actual exists only to satisfy the
 * expect/actual contract; it is never instantiated on the web target.
 */
actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        throw UnsupportedOperationException(
            "SQLite persistence is not available on the web demo build; use in-memory repositories instead."
        )
    }
}
