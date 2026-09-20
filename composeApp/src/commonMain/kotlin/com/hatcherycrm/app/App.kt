package com.hatcherycrm.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.hatcherycrm.app.data.AppRepositories
import com.hatcherycrm.app.data.newId
import com.hatcherycrm.app.ui.BatchFormScreen
import com.hatcherycrm.app.ui.BatchListScreen
import com.hatcherycrm.app.ui.CustomerFormScreen
import com.hatcherycrm.app.ui.CustomerListScreen
import com.hatcherycrm.app.ui.DashboardScreen
import com.hatcherycrm.app.ui.OrderFormScreen
import com.hatcherycrm.app.ui.OrderListScreen
import com.hatcherycrm.app.ui.Screen


/**
 * Root composable shared across Android, iOS and Desktop.
 * Holds simple in-memory navigation state and re-reads from the repositories
 * after each mutation (fine for this app's current scale).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(repositories: AppRepositories) {
    MaterialTheme {
        val customerRepository = repositories.customers
        val batchRepository = repositories.batches
        val orderRepository = repositories.orders

        var screen by remember { mutableStateOf<Screen>(Screen.Dashboard) }
        var customers by remember { mutableStateOf(customerRepository.getAllCustomers()) }
        var batches by remember { mutableStateOf(batchRepository.getAllBatches()) }
        var orders by remember { mutableStateOf(orderRepository.getAllOrders()) }

        fun refreshAll() {
            customers = customerRepository.getAllCustomers()
            batches = batchRepository.getAllBatches()
            orders = orderRepository.getAllOrders()
        }

        val title = when (screen) {
            Screen.Dashboard -> "Hatchery CRM"
            Screen.CustomerList -> "Customers"
            Screen.CustomerForm -> "Add Customer"
            Screen.BatchList -> "Hatch Batches"
            Screen.BatchForm -> "Add Hatch Batch"
            Screen.OrderList -> "Orders"
            Screen.OrderForm -> "Add Order"
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        if (screen != Screen.Dashboard) {
                            IconButton(onClick = {
                                screen = when (screen) {
                                    Screen.CustomerForm -> Screen.CustomerList
                                    Screen.BatchForm -> Screen.BatchList
                                    Screen.OrderForm -> Screen.OrderList
                                    else -> Screen.Dashboard
                                }
                            }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                            }
                        }
                    }
                )
            }
        ) { padding ->
            when (screen) {
                Screen.Dashboard -> DashboardScreen(
                    customerCount = customers.size,
                    batchCount = batches.size,
                    orderCount = orders.size,
                    onOpenCustomers = { screen = Screen.CustomerList },
                    onOpenBatches = { screen = Screen.BatchList },
                    onOpenOrders = { screen = Screen.OrderList },
                    modifier = Modifier.fillMaxSize().padding(padding)
                )

                Screen.CustomerList -> CustomerListScreen(
                    customers = customers,
                    onAddClick = { screen = Screen.CustomerForm },
                    onDeleteClick = { customer ->
                        customerRepository.deleteCustomer(customer.id)
                        refreshAll()
                    },
                    modifier = Modifier.padding(padding)
                )

                Screen.CustomerForm -> CustomerFormScreen(
                    modifier = Modifier.padding(padding),
                    onSave = { result ->
                        customerRepository.addCustomer(
                            id = newId(),
                            name = result.name,
                            phone = result.phone,
                            email = result.email,
                            address = result.address,
                            notes = result.notes
                        )
                        refreshAll()
                        screen = Screen.CustomerList
                    }
                )

                Screen.BatchList -> BatchListScreen(
                    batches = batches,
                    onAddClick = { screen = Screen.BatchForm },
                    onDeleteClick = { batch ->
                        batchRepository.deleteBatch(batch.id)
                        refreshAll()
                    },
                    modifier = Modifier.padding(padding)
                )

                Screen.BatchForm -> BatchFormScreen(
                    modifier = Modifier.padding(padding),
                    onSave = { result ->
                        batchRepository.addBatch(
                            id = newId(),
                            species = result.species,
                            eggCount = result.eggCount,
                            startDateEpochDay = result.startDateEpochDay,
                            expectedHatchEpochDay = result.expectedHatchEpochDay,
                            status = result.status
                        )
                        refreshAll()
                        screen = Screen.BatchList
                    }
                )

                Screen.OrderList -> OrderListScreen(
                    orders = orders,
                    customerNameFor = { id -> customers.find { it.id == id }?.name ?: "Unknown" },
                    onAddClick = { screen = Screen.OrderForm },
                    onDeleteClick = { order ->
                        orderRepository.deleteOrder(order.id)
                        refreshAll()
                    },
                    modifier = Modifier.padding(padding)
                )

                Screen.OrderForm -> OrderFormScreen(
                    customers = customers,
                    batches = batches,
                    modifier = Modifier.padding(padding),
                    onSave = { result ->
                        orderRepository.addOrder(
                            id = newId(),
                            customerId = result.customerId,
                            batchId = result.batchId,
                            quantity = result.quantity,
                            orderDateEpochDay = result.orderDateEpochDay,
                            fulfilled = result.fulfilled
                        )
                        refreshAll()
                        screen = Screen.OrderList
                    }
                )
            }
        }
    }
}

