package com.hatcherycrm.app.data

import com.hatcherycrm.app.db.HatchBatch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/**
 * Simple in-memory implementation used only by the web (wasmJs) target for demo/testing
 * purposes. Data resets on page reload — Android/iOS/Desktop use the SQLite-backed
 * implementation instead, which persists to disk.
 */
class InMemoryBatchRepository : BatchRepository {
    private val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toEpochDays().toLong()

    private val batches = mutableListOf(
        HatchBatch(
            id = "seed-1",
            species = "Broiler Chicks",
            eggCount = 5000,
            startDateEpochDay = today - 5,
            expectedHatchEpochDay = today + 16,
            status = "Incubating"
        )
    )

    override fun getAllBatches(): List<HatchBatch> = batches.sortedByDescending { it.startDateEpochDay }

    override fun addBatch(
        id: String,
        species: String,
        eggCount: Long,
        startDateEpochDay: Long,
        expectedHatchEpochDay: Long,
        status: String
    ) {
        batches.add(HatchBatch(id, species, eggCount, startDateEpochDay, expectedHatchEpochDay, status))
    }

    override fun deleteBatch(id: String) {
        batches.removeAll { it.id == id }
    }
}
