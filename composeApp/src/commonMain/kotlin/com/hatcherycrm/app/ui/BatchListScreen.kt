package com.hatcherycrm.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hatcherycrm.app.db.HatchBatch
import kotlinx.datetime.LocalDate

@Composable
fun BatchListScreen(
    batches: List<HatchBatch>,
    onAddClick: () -> Unit,
    onDeleteClick: (HatchBatch) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add batch")
            }
        }
    ) { padding ->
        if (batches.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hatch batches yet. Tap + to add one.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(batches) { batch ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(batch.species, style = MaterialTheme.typography.titleSmall)
                                Text(
                                    "${batch.eggCount} eggs · ${batch.status}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "Start: ${LocalDate.fromEpochDays(batch.startDateEpochDay.toInt())}  " +
                                        "Expected hatch: ${LocalDate.fromEpochDays(batch.expectedHatchEpochDay.toInt())}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            IconButton(onClick = { onDeleteClick(batch) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete ${batch.species} batch")
                            }
                        }
                    }
                }
            }
        }
    }
}
