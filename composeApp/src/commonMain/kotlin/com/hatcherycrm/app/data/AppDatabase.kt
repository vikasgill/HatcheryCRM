package com.hatcherycrm.app.data

import com.hatcherycrm.app.db.HatcheryDatabase

/**
 * Holds the single shared SQLDelight database instance for the app.
 * All repositories should read from this instead of opening their own connection.
 */
class AppDatabase(driverFactory: DatabaseDriverFactory) {
    val instance = HatcheryDatabase(driverFactory.createDriver())
}
