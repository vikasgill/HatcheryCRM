package com.hatcherycrm.app.data

import com.hatcherycrm.app.db.Customer

interface CustomerRepository {
    fun getAllCustomers(): List<Customer>

    fun addCustomer(id: String, name: String, phone: String?, email: String?, address: String?, notes: String?)

    fun deleteCustomer(id: String)
}

class SqlDelightCustomerRepository(database: AppDatabase) : CustomerRepository {
    private val queries = database.instance.hatcheryQueries

    override fun getAllCustomers() = queries.selectAllCustomers().executeAsList()

    override fun addCustomer(id: String, name: String, phone: String?, email: String?, address: String?, notes: String?) {
        queries.insertCustomer(id, name, phone, email, address, notes)
    }

    override fun deleteCustomer(id: String) {
        queries.deleteCustomer(id)
    }
}
