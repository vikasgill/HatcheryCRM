package com.hatcherycrm.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.hatcherycrm.app.data.AppDatabase
import com.hatcherycrm.app.data.AppRepositories
import com.hatcherycrm.app.data.DatabaseDriverFactory
import com.hatcherycrm.app.data.SqlDelightBatchRepository
import com.hatcherycrm.app.data.SqlDelightCustomerRepository
import com.hatcherycrm.app.data.SqlDelightOrderRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase(DatabaseDriverFactory(applicationContext))
        val repositories = AppRepositories(
            customers = SqlDelightCustomerRepository(database),
            batches = SqlDelightBatchRepository(database),
            orders = SqlDelightOrderRepository(database)
        )
        setContent {
            App(repositories)
        }
    }
}
