package com.hatcherycrm.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hatcherycrm.app.data.CustomerRepository
import com.hatcherycrm.app.data.DatabaseDriverFactory

/**
 * Root composable shared across Android, iOS and Desktop.
 * Wire your navigation/screens here as the app grows.
 */
@Composable
fun App(driverFactory: DatabaseDriverFactory) {
    MaterialTheme {
        val repository = remember { CustomerRepository(driverFactory) }
        val customers = remember { repository.getAllCustomers() }

        Scaffold(
            topBar = { TopAppBar(title = { Text("Hatchery CRM") }) }
        ) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Customers", style = MaterialTheme.typography.titleMedium)
                if (customers.isEmpty()) {
                    Text("No customers yet. Add your first one to get started.")
                } else {
                    LazyColumn {
                        items(customers) { customer ->
                            Text(customer.name)
                            Divider()
                        }
                    }
                }
            }
        }
    }
}
