package com.hatcherycrm.app.data

import com.hatcherycrm.app.db.Order_
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/**
 * Simple in-memory implementation used only by the web (wasmJs) target for demo/testing
 * purposes. Data resets on page reload — Android/iOS/Desktop use the SQLite-backed
 * implementation instead, which persists to disk.
 */
class InMemoryOrderRepository : OrderRepository {
    private val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toEpochDays().toLong()

    private val orders = mutableListOf(
        Order_(
            id = "seed-1",
            customerId = "seed-2",
            batchId = "seed-1",
            quantity = 500,
            orderDateEpochDay = today - 2,
            fulfilled = false
        )
    )

    override fun getAllOrders(): List<Order_> = orders.sortedByDescending { it.orderDateEpochDay }

    override fun addOrder(
        id: String,
        customerId: String,
        batchId: String?,
        quantity: Long,
        orderDateEpochDay: Long,
        fulfilled: Boolean
    ) {
        orders.add(Order_(id, customerId, batchId, quantity, orderDateEpochDay, fulfilled))
    }

    override fun deleteOrder(id: String) {
        orders.removeAll { it.id == id }
    }
}
