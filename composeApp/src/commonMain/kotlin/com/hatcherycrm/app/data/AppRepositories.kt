package com.hatcherycrm.app.data

/**
 * Bundles the three repositories the app needs. Each platform's entry point
 * constructs this with the appropriate backing implementation:
 * SQLite-backed on Android/iOS/Desktop, in-memory on the web (wasmJs) target.
 */
class AppRepositories(
    val customers: CustomerRepository,
    val batches: BatchRepository,
    val orders: OrderRepository
)
