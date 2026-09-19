package com.hatcherycrm.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hatcherycrm.app.db.Customer
import com.hatcherycrm.app.db.HatchBatch
import kotlinx.datetime.LocalDate

data class OrderFormResult(
    val customerId: String,
    val batchId: String?,
    val quantity: Long,
    val orderDateEpochDay: Long,
    val fulfilled: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderFormScreen(
    customers: List<Customer>,
    batches: List<HatchBatch>,
    onSave: (OrderFormResult) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var selectedBatch by remember { mutableStateOf<HatchBatch?>(null) }
    var quantity by remember { mutableStateOf("") }
    var orderDate by remember { mutableStateOf("") }
    var fulfilled by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var customerMenuExpanded by remember { mutableStateOf(false) }
    var batchMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = customerMenuExpanded,
            onExpandedChange = { customerMenuExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedCustomer?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Customer *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerMenuExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = customerMenuExpanded,
                onDismissRequest = { customerMenuExpanded = false }
            ) {
                if (customers.isEmpty()) {
                    DropdownMenuItem(text = { Text("No customers yet — add one first") }, onClick = {})
                }
                customers.forEach { customer ->
                    DropdownMenuItem(
                        text = { Text(customer.name) },
                        onClick = {
                            selectedCustomer = customer
                            customerMenuExpanded = false
                        }
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = batchMenuExpanded,
            onExpandedChange = { batchMenuExpanded = it }
        ) {
            OutlinedTextField(
                value = selectedBatch?.species ?: "None",
                onValueChange = {},
                readOnly = true,
                label = { Text("Hatch Batch (optional)") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = batchMenuExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = batchMenuExpanded,
                onDismissRequest = { batchMenuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("None") },
                    onClick = {
                        selectedBatch = null
                        batchMenuExpanded = false
                    }
                )
                batches.forEach { batch ->
                    DropdownMenuItem(
                        text = { Text("${batch.species} (${batch.eggCount} eggs)") },
                        onClick = {
                            selectedBatch = batch
                            batchMenuExpanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it.filter { c -> c.isDigit() } },
            label = { Text("Quantity *") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = orderDate,
            onValueChange = { orderDate = it },
            label = { Text("Order Date (YYYY-MM-DD) *") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = fulfilled, onCheckedChange = { fulfilled = it })
            Text("Fulfilled")
        }
        errorMessage?.let { Text(it, color = androidx.compose.ui.graphics.Color.Red) }
        Button(
            onClick = {
                val quantityLong = quantity.toLongOrNull()
                val date = runCatching { LocalDate.parse(orderDate) }.getOrNull()
                when {
                    selectedCustomer == null -> errorMessage = "Select a customer"
                    quantityLong == null -> errorMessage = "Enter a valid quantity"
                    date == null -> errorMessage = "Enter a valid order date (YYYY-MM-DD)"
                    else -> {
                        errorMessage = null
                        onSave(
                            OrderFormResult(
                                customerId = selectedCustomer!!.id,
                                batchId = selectedBatch?.id,
                                quantity = quantityLong,
                                orderDateEpochDay = date.toEpochDays().toLong(),
                                fulfilled = fulfilled
                            )
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Order")
        }
    }
}
