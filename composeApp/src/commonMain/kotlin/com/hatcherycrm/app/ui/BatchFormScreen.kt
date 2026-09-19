package com.hatcherycrm.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate

data class BatchFormResult(
    val species: String,
    val eggCount: Long,
    val startDateEpochDay: Long,
    val expectedHatchEpochDay: Long,
    val status: String
)

/**
 * Dates are entered as free text (YYYY-MM-DD) to avoid pulling in a
 * platform-specific date picker dependency for this first pass.
 */
@Composable
fun BatchFormScreen(
    onSave: (BatchFormResult) -> Unit,
    modifier: Modifier = Modifier
) {
    var species by remember { mutableStateOf("") }
    var eggCount by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var expectedHatchDate by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Incubating") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = species,
            onValueChange = { species = it },
            label = { Text("Species *") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = eggCount,
            onValueChange = { eggCount = it.filter { c -> c.isDigit() } },
            label = { Text("Egg Count *") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = { Text("Start Date (YYYY-MM-DD) *") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = expectedHatchDate,
            onValueChange = { expectedHatchDate = it },
            label = { Text("Expected Hatch Date (YYYY-MM-DD) *") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = status,
            onValueChange = { status = it },
            label = { Text("Status") },
            modifier = Modifier.fillMaxWidth()
        )
        errorMessage?.let { Text(it, color = androidx.compose.ui.graphics.Color.Red) }
        Button(
            onClick = {
                val eggCountLong = eggCount.toLongOrNull()
                val start = runCatching { LocalDate.parse(startDate) }.getOrNull()
                val expected = runCatching { LocalDate.parse(expectedHatchDate) }.getOrNull()
                when {
                    species.isBlank() -> errorMessage = "Species is required"
                    eggCountLong == null -> errorMessage = "Enter a valid egg count"
                    start == null -> errorMessage = "Enter a valid start date (YYYY-MM-DD)"
                    expected == null -> errorMessage = "Enter a valid expected hatch date (YYYY-MM-DD)"
                    else -> {
                        errorMessage = null
                        onSave(
                            BatchFormResult(
                                species = species.trim(),
                                eggCount = eggCountLong,
                                startDateEpochDay = start.toEpochDays().toLong(),
                                expectedHatchEpochDay = expected.toEpochDays().toLong(),
                                status = status.trim().ifBlank { "Incubating" }
                            )
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Batch")
        }
    }
}
