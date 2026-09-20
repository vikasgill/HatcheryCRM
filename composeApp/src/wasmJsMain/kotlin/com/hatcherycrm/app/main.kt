package com.hatcherycrm.app

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.hatcherycrm.app.data.AppRepositories
import com.hatcherycrm.app.data.InMemoryBatchRepository
import com.hatcherycrm.app.data.InMemoryCustomerRepository
import com.hatcherycrm.app.data.InMemoryOrderRepository
import kotlinx.browser.document

/**
 * Web entry point. Uses in-memory repositories (see InMemory*Repository) instead of
 * SQLite — this is a demo/testing build only, data resets on page reload. The native
 * Android/iOS/Desktop builds persist data locally via SQLDelight.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val repositories = AppRepositories(
        customers = InMemoryCustomerRepository(),
        batches = InMemoryBatchRepository(),
        orders = InMemoryOrderRepository()
    )
    ComposeViewport(document.body!!) {
        App(repositories)
    }
}
