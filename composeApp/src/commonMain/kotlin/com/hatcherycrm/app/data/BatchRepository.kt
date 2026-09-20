package com.hatcherycrm.app.data

import com.hatcherycrm.app.db.HatchBatch

interface BatchRepository {
    fun getAllBatches(): List<HatchBatch>

    fun addBatch(
        id: String,
        species: String,
        eggCount: Long,
        startDateEpochDay: Long,
        expectedHatchEpochDay: Long,
        status: String
    )

    fun deleteBatch(id: String)
}

class SqlDelightBatchRepository(database: AppDatabase) : BatchRepository {
    private val queries = database.instance.hatcheryQueries

    override fun getAllBatches() = queries.selectAllBatches().executeAsList()

    override fun addBatch(
        id: String,
        species: String,
        eggCount: Long,
        startDateEpochDay: Long,
        expectedHatchEpochDay: Long,
        status: String
    ) {
        queries.insertBatch(id, species, eggCount, startDateEpochDay, expectedHatchEpochDay, status)
    }

    override fun deleteBatch(id: String) {
        queries.deleteBatch(id)
    }
}
