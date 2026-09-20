package com.hatcherycrm.app.data

import com.hatcherycrm.app.db.Order_

interface OrderRepository {
    fun getAllOrders(): List<Order_>

    fun addOrder(
        id: String,
        customerId: String,
        batchId: String?,
        quantity: Long,
        orderDateEpochDay: Long,
        fulfilled: Boolean
    )

    fun deleteOrder(id: String)
}

class SqlDelightOrderRepository(database: AppDatabase) : OrderRepository {
    private val queries = database.instance.hatcheryQueries

    override fun getAllOrders() = queries.selectAllOrders().executeAsList()

    override fun addOrder(
        id: String,
        customerId: String,
        batchId: String?,
        quantity: Long,
        orderDateEpochDay: Long,
        fulfilled: Boolean
    ) {
        queries.insertOrder(id, customerId, batchId, quantity, orderDateEpochDay, fulfilled)
    }

    override fun deleteOrder(id: String) {
        queries.deleteOrder(id)
    }
}
