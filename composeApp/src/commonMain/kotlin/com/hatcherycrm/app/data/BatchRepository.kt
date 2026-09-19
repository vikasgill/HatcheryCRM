package com.hatcherycrm.app.data

class BatchRepository(database: AppDatabase) {
    private val queries = database.instance.hatcheryQueries

    fun getAllBatches() = queries.selectAllBatches().executeAsList()

    fun addBatch(
        id: String,
        species: String,
        eggCount: Long,
        startDateEpochDay: Long,
        expectedHatchEpochDay: Long,
        status: String
    ) {
        queries.insertBatch(id, species, eggCount, startDateEpochDay, expectedHatchEpochDay, status)
    }

    fun deleteBatch(id: String) {
        queries.deleteBatch(id)
    }
}
