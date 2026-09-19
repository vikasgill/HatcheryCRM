package com.hatcherycrm.app.ui

/**
 * Simple sealed-class navigation model for this small app.
 * Avoids pulling in a navigation library while the screen count is small.
 */
sealed class Screen {
    data object Dashboard : Screen()

    data object CustomerList : Screen()
    data object CustomerForm : Screen()

    data object BatchList : Screen()
    data object BatchForm : Screen()

    data object OrderList : Screen()
    data object OrderForm : Screen()
}
