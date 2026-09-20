package com.hatcherycrm.app

import androidx.compose.ui.window.ComposeUIViewController
import com.hatcherycrm.app.data.AppDatabase
import com.hatcherycrm.app.data.AppRepositories
import com.hatcherycrm.app.data.DatabaseDriverFactory
import com.hatcherycrm.app.data.SqlDelightBatchRepository
import com.hatcherycrm.app.data.SqlDelightCustomerRepository
import com.hatcherycrm.app.data.SqlDelightOrderRepository
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    val database = AppDatabase(DatabaseDriverFactory())
    val repositories = AppRepositories(
        customers = SqlDelightCustomerRepository(database),
        batches = SqlDelightBatchRepository(database),
        orders = SqlDelightOrderRepository(database)
    )
    App(repositories)
}
