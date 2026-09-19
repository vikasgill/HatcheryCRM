package com.hatcherycrm.app.data

import kotlin.random.Random
import kotlinx.datetime.Clock

/**
 * Generates a unique-enough ID for local records without relying on
 * platform-specific UUID APIs (java.util.UUID isn't available in commonMain).
 */
fun newId(): String {
    val timestamp = Clock.System.now().toEpochMilliseconds()
    val randomPart = Random.nextLong().toString(16)
    return "$timestamp-$randomPart"
}
