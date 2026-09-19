package com.hatcherycrm.app.data

class CustomerRepository(database: AppDatabase) {
    private val queries = database.instance.hatcheryQueries

    fun getAllCustomers() = queries.selectAllCustomers().executeAsList()

    fun addCustomer(id: String, name: String, phone: String?, email: String?, address: String?, notes: String?) {
        queries.insertCustomer(id, name, phone, email, address, notes)
    }

    fun deleteCustomer(id: String) {
        queries.deleteCustomer(id)
    }
}
