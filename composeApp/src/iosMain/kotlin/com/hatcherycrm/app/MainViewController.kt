package com.hatcherycrm.app

import androidx.compose.ui.window.ComposeUIViewController
import com.hatcherycrm.app.data.DatabaseDriverFactory
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    App(DatabaseDriverFactory())
}
