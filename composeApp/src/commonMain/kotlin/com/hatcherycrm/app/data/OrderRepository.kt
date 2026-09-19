package com.hatcherycrm.app.data

class OrderRepository(database: AppDatabase) {
    private val queries = database.instance.hatcheryQueries

    fun getAllOrders() = queries.selectAllOrders().executeAsList()

    fun addOrder(
        id: String,
        customerId: String,
        batchId: String?,
        quantity: Long,
        orderDateEpochDay: Long,
        fulfilled: Boolean
    ) {
        queries.insertOrder(id, customerId, batchId, quantity, orderDateEpochDay, fulfilled)
    }

    fun deleteOrder(id: String) {
        queries.deleteOrder(id)
    }
}
