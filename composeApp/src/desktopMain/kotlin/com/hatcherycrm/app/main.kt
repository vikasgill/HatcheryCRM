package com.hatcherycrm.app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.hatcherycrm.app.data.DatabaseDriverFactory

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Hatchery CRM") {
        App(DatabaseDriverFactory())
    }
}
