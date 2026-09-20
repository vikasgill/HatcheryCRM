package com.hatcherycrm.app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.hatcherycrm.app.data.AppDatabase
import com.hatcherycrm.app.data.AppRepositories
import com.hatcherycrm.app.data.DatabaseDriverFactory
import com.hatcherycrm.app.data.SqlDelightBatchRepository
import com.hatcherycrm.app.data.SqlDelightCustomerRepository
import com.hatcherycrm.app.data.SqlDelightOrderRepository

fun main() = application {
    val database = AppDatabase(DatabaseDriverFactory())
    val repositories = AppRepositories(
        customers = SqlDelightCustomerRepository(database),
        batches = SqlDelightBatchRepository(database),
        orders = SqlDelightOrderRepository(database)
    )
    Window(onCloseRequest = ::exitApplication, title = "Hatchery CRM") {
        App(repositories)
    }
}
