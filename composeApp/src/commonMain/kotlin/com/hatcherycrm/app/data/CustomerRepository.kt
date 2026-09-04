package com.hatcherycrm.app.data

import com.hatcherycrm.app.db.HatcheryDatabase

class CustomerRepository(driverFactory: DatabaseDriverFactory) {
    private val database = HatcheryDatabase(driverFactory.createDriver())
    private val queries = database.hatcheryDatabaseQueries

    fun getAllCustomers() = queries.selectAllCustomers().executeAsList()

    fun addCustomer(id: String, name: String, phone: String?, email: String?, address: String?, notes: String?) {
        queries.insertCustomer(id, name, phone, email, address, notes)
    }

    fun deleteCustomer(id: String) {
        queries.deleteCustomer(id)
    }
}
