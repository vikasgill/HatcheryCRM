package com.hatcherycrm.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    customerCount: Int,
    batchCount: Int,
    orderCount: Int,
    onOpenCustomers: () -> Unit,
    onOpenBatches: () -> Unit,
    onOpenOrders: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Overview", style = MaterialTheme.typography.titleMedium)

        DashboardCard(
            title = "Customers",
            count = customerCount,
            buttonLabel = "Manage Customers",
            onClick = onOpenCustomers
        )
        DashboardCard(
            title = "Hatch Batches",
            count = batchCount,
            buttonLabel = "Manage Batches",
            onClick = onOpenBatches
        )
        DashboardCard(
            title = "Orders",
            count = orderCount,
            buttonLabel = "Manage Orders",
            onClick = onOpenOrders
        )
    }
}

@Composable
private fun DashboardCard(
    title: String,
    count: Int,
    buttonLabel: String,
    onClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(PaddingValues(16.dp)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text("$count total", style = MaterialTheme.typography.bodyMedium)
            Button(onClick = onClick) {
                Text(buttonLabel)
            }
        }
    }
}
