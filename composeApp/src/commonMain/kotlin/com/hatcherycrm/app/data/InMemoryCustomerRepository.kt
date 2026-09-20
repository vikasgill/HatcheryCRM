package com.hatcherycrm.app.data

import com.hatcherycrm.app.db.Customer

/**
 * Simple in-memory implementation used only by the web (wasmJs) target for demo/testing
 * purposes. Data resets on page reload — Android/iOS/Desktop use the SQLite-backed
 * implementation instead, which persists to disk.
 */
class InMemoryCustomerRepository : CustomerRepository {
    private val customers = mutableListOf(
        Customer(id = "seed-1", name = "Rajesh Kumar", phone = "+91 98765 43210", email = "rajesh.kumar@example.com", address = null, notes = null),
        Customer(id = "seed-2", name = "Janta Foods", phone = "+91 91234 56789", email = "orders@jantafoods.com", address = null, notes = null)
    )

    override fun getAllCustomers(): List<Customer> = customers.sortedBy { it.name }

    override fun addCustomer(id: String, name: String, phone: String?, email: String?, address: String?, notes: String?) {
        customers.add(Customer(id, name, phone, email, address, notes))
    }

    override fun deleteCustomer(id: String) {
        customers.removeAll { it.id == id }
    }
}
